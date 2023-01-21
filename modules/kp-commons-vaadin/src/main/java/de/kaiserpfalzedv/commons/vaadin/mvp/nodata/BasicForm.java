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

package de.kaiserpfalzedv.commons.vaadin.mvp.nodata;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import de.kaiserpfalzedv.commons.vaadin.InvalidComponentTypeException;
import de.kaiserpfalzedv.commons.vaadin.nav.MainLayout;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@SuppressWarnings("unused")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Slf4j
public abstract class BasicForm extends FormLayout {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    protected final FrontendUser user;


    @Getter
    protected TabSheet tabs;
    protected ThemableLayout buttonBar;

    protected Button execute;
    protected Button reset;
    protected Button close;

    public BasicForm(
            @NotNull final FrontendUser user
    ) {
        this.user = user;

        setResponsiveSteps(
                new ResponsiveStep("200px", 1),
                new ResponsiveStep("600px", 3)
        );

        tabs = createTabs();
        buttonBar = createButtonsLayout();
    }

    private TabSheet createTabs() {
        TabSheet result = new TabSheet();
        result.setWidth("100%");
        result.addSelectedChangeListener(e -> ComponentUtil.fireEvent(
                UI.getCurrent(),
                new MainLayout.PageTitleUpdateEvent(this, false)
        ));

        return result;
    }


    protected void addTab(final BasicFormTab tab) {
        log.trace("Adding tab to form. form={}, tab={}", this, tab);
        tabs.add(getTranslation(tab.getI18nKey() + ".caption"), tab);
    }

    protected void clearTabs() {
        tabs = createTabs();
    }


    public String getTabTitle() {
        log.debug("retrieving tab title. selected={}, children={}", tabs.getSelectedIndex(), tabs.getElement().getChildCount());

        if (tabs != null && tabs.getSelectedIndex() != -1) {
            return tabs.getSelectedTab().getLabel();
        }

        return "";
    }

    private ThemableLayout createButtonsLayout() {
        execute = new Button(getTranslation("buttons.execute.caption"));
        execute.setTooltipText(getTranslation("buttons.execute.help"));
        reset = new Button(getTranslation("buttons.reset.caption"));
        reset.setTooltipText(getTranslation("buttons.reset.help"));
        close = new Button(getTranslation("buttons.cancel.caption"));
        close.setTooltipText(getTranslation("buttons.cancel.help"));

        execute.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        reset.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        execute.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        execute.addClickListener(event -> fireEvent(new ExecuteEvent(this)));
        reset.addClickListener(event -> fireEvent(new ResetEvent(this)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return createButtons();
    }

    protected ThemableLayout createButtons() {
        return new HorizontalLayout(execute, reset, close);
    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        log.trace("Form attached. initial={}, form={}, event={}",
                attachEvent.isInitialAttach(), this, attachEvent);

        super.onAttach(attachEvent);

        tabs = createTabs();
        add(tabs, 3);

        try {
            add((Component) buttonBar, 3);
        } catch (ClassCastException e) {
            log.error("The generated button bar is not of type 'Component' ('{}').",
                    buttonBar.getClass().getSimpleName());

            throw new InvalidComponentTypeException(Component.class, buttonBar.getClass(), e);
        }


        ComponentUtil.fireEvent(UI.getCurrent(), new MainLayout.PageTitleUpdateEvent(this, false));
    }

    @Override
    public void onDetach(DetachEvent detachEvent) {
        log.trace("Form detached. form={}, event={}", this, detachEvent);

        super.onDetach(detachEvent);

        removeAll();
    }


    protected void translate(Component component, final String code) {
        if (component == null) {
            return;
        }

        if (HasLabel.class.isAssignableFrom(component.getClass())) {
            ((HasLabel)component).setLabel(getTranslation(user.getLocale(), code + ".caption"));
        }

        if (HasHelper.class.isAssignableFrom(component.getClass())) {
            ((HasHelper)component).setHelperText(getTranslation(user.getLocale(), code + ".help"));
        }
    }



    @Getter
    public static abstract class FormEvent extends ComponentEvent<BasicForm> {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public FormEvent(BasicForm source) {
            super(source, false);
        }
    }

    public static class ExecuteEvent extends FormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public <T extends Serializable> ExecuteEvent(BasicForm source) {
            super(source);
        }
    }

    public static class CloseEvent extends FormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public <T extends Serializable> CloseEvent(BasicForm source) {
            super(source);
        }
    }

    public static class ResetEvent extends FormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public <T extends Serializable> ResetEvent(BasicForm source) {
            super(source);
        }
    }

}
