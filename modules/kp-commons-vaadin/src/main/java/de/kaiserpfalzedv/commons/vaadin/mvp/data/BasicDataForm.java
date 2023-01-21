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

package de.kaiserpfalzedv.commons.vaadin.mvp.data;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.data.binder.Binder;
import de.kaiserpfalzedv.commons.vaadin.mvp.nodata.BasicForm;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@SuppressWarnings("unused")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public abstract class BasicDataForm<T extends Serializable> extends BasicForm {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    protected T data;

    @Getter(AccessLevel.PUBLIC)
    protected final Binder<T> binder;


    protected Button save;
    protected Button delete;

    public BasicDataForm(
            @NotNull final FrontendUser user,
            @NotNull final Binder<T> binder
    ) {
        super(user);
        this.binder = binder;
    }

    protected abstract void bind();


    @Override
    protected ThemableLayout createButtons() {
        save = new Button(getTranslation("buttons.save.caption"));
        save.setTooltipText(getTranslation("buttons.save.help"));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        delete = new Button(getTranslation("buttons.delete.caption"));
        delete.setTooltipText(getTranslation("buttons.delete.help"));
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> fireEvent(new SaveEvent(this, data)));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, data)));
        reset.addClickListener(event -> fireEvent(new ResetEvent(this)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, reset, close);
    }


    @Getter
    public static abstract class FormEvent<T extends Serializable> extends BasicForm.FormEvent {
        private final T data;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param data   the current data
         */
        public FormEvent(BasicDataForm<T> source, T data) {
            super(source);

            this.data = data;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class SaveEvent extends FormEvent {
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
    public static class DeleteEvent extends FormEvent {
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
    public static class CloseEvent extends FormEvent {
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
    public static class ResetEvent extends FormEvent {
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
