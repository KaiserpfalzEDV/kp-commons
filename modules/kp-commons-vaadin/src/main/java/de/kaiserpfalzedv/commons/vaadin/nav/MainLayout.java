/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.quarkus.annotation.UIScoped;
import de.kaiserpfalzedv.commons.vaadin.notifications.ErrorNotification;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScoped
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout {

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    FrontendUser user;

    private H2 viewTitle;

    private final AccessAnnotationChecker accessChecker;


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

        // FIXME 2023-01-20 klenkes74 Insert the code to provide the dynamical loaded views in this application

        return nav;
    }

    private void addViewToNavigation(AppNav nav, @SuppressWarnings("rawtypes") final Class view, @SuppressWarnings("SameParameterValue") final String icon) {
        if (accessChecker.hasAccess(view)) {
            //noinspection unchecked
            nav.addItem(new AppNavItem(getTranslation(view.getSimpleName()), view, icon));
        } else {
            log.debug("User has no access to view. user='{}', roles={}, view={}",
                    user.getName(), user.getRoles(), view.getSimpleName());
        }
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
        userName.getSubMenu().addItem("Roles", e -> {
            log.info("Roles of logged in user. roles={}",
                    String.join(", ", user.getRoles())
            );
            Notification.show(
                    "Roles: " +
                            String.join(", ", user.getRoles())

            );
        });
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
     * that interface). Otherwise the annotated {@link PageTitle} is used. If even that fails, the
     * {@link Class#getSimpleName()} will be fead into the {@link #getTranslation(Object, Object...)} method as key and
     * the result is displayed as PageTitle.
     *
     * @return The pagetitle to be displayed.
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
                getTranslation("error.session_expiry.notification", IdleNotification.MessageFormatting.SECS_TO_TIMEOUT));
        idleNotification.addExtendSessionButton(getTranslation("error.session_expiry.extension"));
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
     * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 2.0.0  2023-01-05
     */
    public static class PageTitleUpdateEvent extends ComponentEvent<FormLayout> {
        public PageTitleUpdateEvent(FormLayout source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
