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
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package de.kaiserpfalzedv.commons.vaadin.mvp.nodata;

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
import java.util.HashMap;

/**
 * <p>BasicViewImpl -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-21
 */
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Slf4j
public abstract class BasicViewImpl extends Div implements BasicView {
    protected final BasicPresenter presenter;

    @ToString.Include
    @EqualsAndHashCode.Include
    protected final BasicForm form;

    protected final HashMap<String, Registration> busRegistration = new HashMap<>(4);

    @EqualsAndHashCode.Include
    protected FrontendUser user;

    public BasicViewImpl(final BasicPresenter presenter, final BasicForm form) {
        this.presenter = presenter;
        presenter.setView(this);
        presenter.setForm(form);
        this.form = form;
    }

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

        registerListener(BasicForm.ExecuteEvent.class, e-> presenter.execute());
        registerListener(BasicForm.CloseEvent.class, e -> presenter.close());
        registerListener(BasicForm.ResetEvent.class, e -> presenter.reset());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void registerListener(Class eventTypeClass, ComponentEventListener<?> listener) {
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
