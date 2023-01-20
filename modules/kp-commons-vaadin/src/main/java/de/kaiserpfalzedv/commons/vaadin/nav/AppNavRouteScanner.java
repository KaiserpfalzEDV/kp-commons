/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package de.kaiserpfalzedv.commons.vaadin.nav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.AnyLiteral;
import de.kaiserpfalzedv.commons.vaadin.i18n.I18nTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>AppNavRouteScanner -- Scans the beans for {@link Component}s annotated with {@link Route} and {@link AppNavRoute}.</p>
 *
 * <p>The scanner scans for all {@link AppNavRoute}s and ensures that they have a {@link Route} annotation.</p>
 *
 * <p>The component used the {@link I18nTranslator} to localize the labels (they are treated as i18n keys).</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-20
 */
@Dependent
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class AppNavRouteScanner implements Serializable, AutoCloseable {

    private final BeanManager beanManager;

    private final Locale locale;
    private final I18nTranslator i18n;


    public Set<Bean<?>> getNavigationTargets() {
        Set<Bean<?>> result = beanManager.getBeans(Component.class, new AppNavRoute.Literal(), new AnyLiteral());
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    /**
     * <p>getNavItems() will deliver a sorted list of {@link AppNavItem}s. The items are sorted by their {@link
     * Priority} (highest first). The {@link I18nTranslator} injected as {@link #i18n} will be used to translate the key
     * if it is set. Otherwise the {@link AppNavRoute#label()} is used directly.</p>
     *
     * <p>Only objects that can be converted to {@link Component} are considered. They need to provide a path without
     * parameters. All components not matching these requirements are ignored.</p>
     *
     * @return List of AppNavItems.
     */
    // TODO 2023-01-20 klenkes74 Implement nested menu structures as offered by AppNavItem.
    public List<AppNavItem> getNavItems() {
        Set<Bean<?>> raw = getNavigationTargets();

        // FIXME 2023-01-20 klenkes74 Insert the code to provide the dynamical loaded views in this application

        List<AppNavItem> result = raw.stream()
                .filter(this::filterComponent) // important filter for mapAppNavRoute
                .filter(this::filterRoute)
                .filter(this::filterLabel)
                .filter(this::filterPath)
                .sorted(this::comparePriority)
                .filter(this::logBean)
                .map(this::mapAppNavRoute)
                .collect(Collectors.toList());


        log.debug("mapped Bean<?> to AppNavItems. raw={}, result={}", raw, result);
        return result;
    }

    private boolean filterComponent(Bean<?> d) {
        if (! Component.class.isAssignableFrom(d.getBeanClass())) {
            log.error("This AppNavRoute annotated class is not of type 'Component'. Can't add it to the navigation. bean={}",
                    d.getBeanClass().getCanonicalName());

            return false;
        }

        return true;
    }

    private boolean filterLabel(Bean<?> d) {
        AppNavRoute route = d.getBeanClass().getAnnotation(AppNavRoute.class);
        if (route.i18n().isBlank() && route.label().equals("No Menu Text defined")) {
            log.error("This AppNavRoute does neither specify a i18n key nor a label. It will not be added to the navigation. bean={}",
                    d.getBeanClass().getCanonicalName());

            return false;
        }

        return true;
    }

    private boolean filterRoute(Bean<?> d) {
        if (d.getBeanClass().getAnnotation(Route.class) == null) {
            log.error("This AppNavRoute is not annotated with a @Route. Can't add it to the navigation. bean={}",
                    d.getBeanClass().getCanonicalName());
            return false;
        }

        return true;
    }

    private boolean filterPath(Bean<?> d) {
        Route route = d.getBeanClass().getAnnotation(Route.class);
        if (route.value().contains(":")) {
            log.error("This AppNavRoute has a path with parameters. Can't add it to the navigation. component={}",
                    d.getBeanClass().getCanonicalName());

            return false;
        }

        return true;
    }

    private boolean logBean(Bean<?> d) {
        if (log.isTraceEnabled()) {
            log.trace("component='{}', priority={}, path='{}', label='{}', i18n='{}', icon='{}'",
                    d.getBeanClass().getCanonicalName(),
                    getPriority(d),
                    d.getBeanClass().getAnnotation(Route.class).value(),
                    d.getBeanClass().getAnnotation(AppNavRoute.class).label(),
                    d.getBeanClass().getAnnotation(AppNavRoute.class).i18n(),
                    d.getBeanClass().getAnnotation(AppNavRoute.class).icon()
            );
        }

        return true;
    }

    private int comparePriority(Bean<?> c1, Bean<?> c2) {
        return getPriority(c2)-getPriority(c1);
    }

    private int getPriority(Bean<?> bean) {
        Priority priority = bean.getBeanClass().getAnnotation(Priority.class);
        return (priority != null) ? priority.value() : AppNavRoute.DEFAULT_APP_NAV_PRIORITY;
    }

    private AppNavItem mapAppNavRoute(Bean<?> bean) {
        AppNavRoute appNavRoute = bean.getBeanClass().getAnnotation(AppNavRoute.class);
        Route route = bean.getBeanClass().getAnnotation(Route.class);

        log.trace("Mapping AppNavRoute to AppNavItem. bean='{}', label='{}', i18n='{}', icon='{}', path='{}'",
                bean.getBeanClass().getCanonicalName(),
                appNavRoute.label(), appNavRoute.i18n(), appNavRoute.icon(), route.value());

        String label = !appNavRoute.i18n().isBlank()
                ? i18n.getTranslation(appNavRoute.i18n(), locale)
                : appNavRoute.label();

        return !appNavRoute.icon().isBlank()
                ? new AppNavItem(label, route.value(), appNavRoute.icon())
                : new AppNavItem(label, route.value());
    }


    @PreDestroy
    public void close() {
    }

}
