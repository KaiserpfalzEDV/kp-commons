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

package de.kaiserpfalzedv.commons.vaadin.mvp;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.binder.Binder;
import de.kaiserpfalzedv.commons.vaadin.nav.MainLayout;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Slf4j
public abstract class BasicDataForm<T extends Serializable> extends FormLayout {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    protected final FrontendUser user;

    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    protected T data;

    @Getter
    protected final Binder<T> binder;


    @Getter
    protected TabSheet tabs;
    protected Component buttonBar;

    protected Button save;
    protected Button reset;
    protected Button delete;
    protected Button close;

    public BasicDataForm(
            @NotNull final FrontendUser user,
            @NotNull final Binder<T> binder
    ) {
        this.user = user;
        this.binder = binder;

        setResponsiveSteps(
                new ResponsiveStep("200px", 1),
                new ResponsiveStep("600px", 3)
        );

        tabs = createTabs();
        buttonBar = createButtonsLayout();
    }

    protected abstract void bind();

    private TabSheet createTabs() {
        TabSheet result = new TabSheet();
        result.setWidth("100%");
        result.addSelectedChangeListener(e -> ComponentUtil.fireEvent(
                UI.getCurrent(),
                new MainLayout.PageTitleUpdateEvent(this, false)
        ));

        return result;
    }


    protected void addTab(final BasicDataFormTab<T> tab) {
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

    private Component createButtonsLayout() {
        save = new Button(getTranslation("buttons.save.caption"));
        save.setTooltipText(getTranslation("buttons.save.help"));
        reset = new Button(getTranslation("buttons.reset.caption"));
        reset.setTooltipText(getTranslation("buttons.reset.help"));
        delete = new Button(getTranslation("buttons.delete.caption"));
        delete.setTooltipText(getTranslation("buttons.delete.help"));
        close = new Button(getTranslation("buttons.cancel.caption"));
        close.setTooltipText(getTranslation("buttons.cancel.help"));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        reset.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> fireEvent(new SaveEvent(this, data)));
        reset.addClickListener(event -> fireEvent(new ResetEvent(this)));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, data)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, reset, delete, close);
    }


    @Override
    public void onAttach(AttachEvent attachEvent) {
        log.trace("Form attached. initial={}, form={}, event={}",
                attachEvent.isInitialAttach(), this, attachEvent);

        super.onAttach(attachEvent);

        tabs = createTabs();
        add(tabs, 3);
        add(buttonBar, 3);


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
    public static abstract class DataFormEvent<T extends Serializable> extends ComponentEvent<BasicDataForm<T>> {
        private final T data;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param data   the current data
         */
        public DataFormEvent(BasicDataForm<T> source, T data) {
            super(source, false);

            this.data = data;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class SaveEvent extends DataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param data   the current data
         */
        public <T extends Serializable> SaveEvent(BasicDataForm<T> source, T data) {
            super(source, data);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class DeleteEvent extends DataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param data   the current data
         */
        public <T extends Serializable> DeleteEvent(BasicDataForm<T> source, T data) {
            super(source, data);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class CloseEvent extends DataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public <T extends Serializable> CloseEvent(BasicDataForm<T> source) {
            super(source, source.getData());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class ResetEvent extends DataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         */
        public <T extends Serializable> ResetEvent(BasicDataForm<T> source) {
            super(source, source.getData());
        }
    }

}
