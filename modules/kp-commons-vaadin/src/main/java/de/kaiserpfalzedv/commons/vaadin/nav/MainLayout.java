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

import com.vaadin.componentfactory.IdleNotification;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.cookieconsent.CookieConsent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.quarkus.annotation.UIScoped;
import de.kaiserpfalzedv.commons.vaadin.notifications.ErrorNotification;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScoped
@AnonymousAllowed
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
public class MainLayout extends AppLayout implements RouterLayout {
    private final AppNavRouteScanner routeScanner;
    @SuppressWarnings("CdiInjectionPointsInspection")
    @ToString.Include
    @Inject
    FrontendUser user;

    private H2 viewTitle;



    @PostConstruct
    public void init() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();


        log.info("Adding idle notification and cookie consent to UI. ui={}", UI.getCurrent());
        UI.getCurrent().add(initiateIdleNotifications());
        UI.getCurrent().add(cookieConsent());
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1(getTranslation("application.title"));
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        List<AppNavItem> entries = routeScanner.getNavItems();
        log.debug("Loaded navigation entries. items={}", entries);

        nav.addItem(entries.toArray(new AppNavItem[0]));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Avatar avatar = new Avatar(user.getName());


        avatar.setImage(user.getAvatar());
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");

        MenuBar userMenu = new MenuBar();
        userMenu.setThemeName("tertiary-inline contrast");

        MenuItem userName = userMenu.addItem("");
        Div div = new Div();
        div.add(avatar);
        div.add(user.getName());
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        userName.add(div);
        userName.getSubMenu().addItem("Sign out", e -> {
            log.info("User logout. event={}", e);
            getUI().ifPresentOrElse(
                    ui -> ui.getPage().setLocation("/logout"),
                    () -> ErrorNotification.showMarkdown("User can't logout. There is no _UI_.")
            );
        });

        //noinspection SpellCheckingInspection
        userName.getSubMenu().getItems().forEach(e -> e.setId("usermenu"));
        layout.add(userMenu);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Reads the title to display. Either retrieves the title from {@link HasDynamicTitle} (when the view does implement
     * that interface), otherwise the annotated {@link PageTitle} is used. If even that fails, the
     * {@link Class#getSimpleName()} will be feed into the {@link #getTranslation(Object, Object...)} method as key and
     * the result is displayed as PageTitle.
     *
     * @return The page title to be displayed.
     */
    private String getCurrentPageTitle() {
        String result;

        if (HasDynamicTitle.class.isAssignableFrom(getContent().getClass())) {
            result = ((HasDynamicTitle)getContent()).getPageTitle();
        } else {
            PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
            if (title != null) {
                result = title.value();
            } else {
                result = getTranslation(getContent().getClass().getSimpleName());
            }
        }

        return result;
    }

    private IdleNotification initiateIdleNotifications() {
        IdleNotification idleNotification = new IdleNotification();

// No. of secs before timeout, at which point the notification is displayed
        idleNotification.setSecondsBeforeNotification(90);
        idleNotification.setMessage(
                getTranslation("session_expiry.error.notification", IdleNotification.MessageFormatting.SECS_TO_TIMEOUT));
        idleNotification.addExtendSessionButton(getTranslation("session_expiry.error.extension"));
        idleNotification.addRedirectButton(getTranslation("buttons.logout.caption"), "logout");
        idleNotification.addCloseButton();
        idleNotification.setExtendSessionOnOutsideClick(false);

        return idleNotification;
    }

    private CookieConsent cookieConsent() {
        final String message = "We are using cookies to make your visit here awesome!";
        final String dismissLabel = "Cool!";
        final String learnMoreLabel = "Why?";
        final String learnMoreLink = "https://vaadin.com/terms-of-service";
        final CookieConsent.Position position = CookieConsent.Position.BOTTOM_RIGHT;

        return new CookieConsent(message, dismissLabel, learnMoreLabel, learnMoreLink, position);
    }


    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);

        ComponentUtil.addListener(
                event.getUI(),
                PageTitleUpdateEvent.class,
                e -> {
                    String title = getCurrentPageTitle();
                    viewTitle.setText(title);
                    UI.getCurrent().getPage().setTitle(title);
                }
        );
    }

    @Override
    protected void onDetach(DetachEvent event) {
        super.onDetach(event);
    }

    /**
     * PageTitleUpdateEvent -- This event triggers a reload of the PageTitle in the MainLayout.
     *
     * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 1.0.0  2023-01-05
     */
    public static class PageTitleUpdateEvent extends ComponentEvent<FormLayout> {
        public PageTitleUpdateEvent(FormLayout source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
