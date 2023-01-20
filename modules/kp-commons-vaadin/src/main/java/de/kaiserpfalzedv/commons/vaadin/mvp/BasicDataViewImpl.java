/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;

/**
 * BasicDataViewImpl -- Basis for the concrete views.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-30
 *
 * @param <T> The data to be displayed
 */
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Slf4j
public abstract class BasicDataViewImpl<T extends Serializable> extends Div implements BasicDataView<T> {
    protected final BasicPresenter<T> presenter;

    @EqualsAndHashCode.Include
    protected final BasicDataForm<T> form;

    @EqualsAndHashCode.Include
    protected FrontendUser user;

    private final HashMap<String, Registration>  busRegistration = new HashMap<>(4);


    public BasicDataViewImpl(final BasicPresenter<T> presenter, final BasicDataForm<T> form) {
        this.presenter = presenter;
        this.form = form;

        presenter.setView(this);
        presenter.setForm(form);

        add(form);
    }

    @Override
    public void setData(T data) {
        form.setData(data);

        updateView();
    }

    public T getData() {
        return form.getData();
    }

    @Override
    public void setFrontendUser(@NotNull final FrontendUser identity) {
        boolean update = false;
        if (user != null && identity.getName().equals(user.getName())) {
            log.trace("The user has changed. old={} new={}", this.user, identity);
            update = true;
        }
        if (user != null && identity.getLocale() != user.getLocale()) {
            log.trace("The locale has changed. old={}, new={}", this.user.getLocale(), identity.getLocale());
            update = true;
        }

        this.user = identity;
        log.trace("Updated identity in view. identity={}, view={}", identity, this);

        if (update) {
            updateView();
        }
    }

    /**
     * Updates the locale or user on the view. Needs to change all labels, ... on the view.
     */
    protected abstract void updateView();


    @Override
    public void onAttach(final AttachEvent attachEvent) {
        log.trace("view attached. view={}, event={}", this, attachEvent);
        super.onAttach(attachEvent);

        registerListener(BasicDataForm.SaveEvent.class, e -> presenter.save());
        registerListener(BasicDataForm.DeleteEvent.class, e -> presenter.delete());
        registerListener(BasicDataForm.CloseEvent.class, e-> presenter.close());
        registerListener(BasicDataForm.ResetEvent.class, e-> presenter.reset());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerListener(Class eventTypeClass, ComponentEventListener<?> listener) {
        busRegistration.put(
                eventTypeClass.getCanonicalName(),
                ComponentUtil.addListener(form, eventTypeClass, listener)
        );
    }

    @Override
    public void onDetach(final DetachEvent detachEvent) {
        log.trace("view detached. view={}, event={}", this, detachEvent);
        super.onDetach(detachEvent);
        busRegistration.values().forEach(Registration::remove);
    }
}
