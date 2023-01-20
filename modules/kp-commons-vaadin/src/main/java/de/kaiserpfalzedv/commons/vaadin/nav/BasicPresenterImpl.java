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

package de.kaiserpfalzedv.commons.vaadin.nav;

import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@Slf4j
public abstract class BasicPresenterImpl<T extends Serializable> implements BasicPresenter<T> {

    @ToString.Include
    protected T data;

    @ToString.Include
    protected  BasicView<T> view;

    @ToString.Include
    protected BasicDataForm<T> form;

    @ToString.Include
    protected FrontendUser user;


    @Override
    abstract public void loadId(@NotNull UUID id);

    abstract public void save();
    abstract public void delete();

    @SuppressWarnings("ConstantConditions")
    public void reset() {
        if (data == null) {
            log.debug("Can't reset data in view since there is no data. presenter={}, view={}, data={}", this, view, data);
            return;
        }

        if (view != null) {
            view.setData(data);

            log.trace("Data reset in view. presenter={}, view={}, data={}", this, view, data);
        } else {
            log.debug("Can't reset data in view since there is no view. presenter={}, view={}, data={}", this, view, data);
        }

    }
    abstract public void close();

    @Override
    public void setData(@NotNull T data) {
        this.data = data;
        log.trace("Data reset in presenter. presenter={}, data={}", this, data);

        reset();
    }

    @Override
    public <V extends BasicView<T>> void setView(@NotNull V view) {
        this.view = view;

        if (data != null) {
            reset();
        }

        if (user != null) {
            view.setFrontendUser(user);
        }

        log.trace("Added view to presenter. presenter={}, view={}, form={}", this, this.view, form);
    }

    @Override
    public <F extends BasicDataForm<T>> void setForm(@NotNull F form) {
        this.form = form;

        if (data != null) {
            form.setData(data);
        }

        log.trace("Added form to presenter. presenter={}, view={}, form={}", this, view, this.form);
    }

    @Inject
    @Override
    public void setFrontendUser(@NotNull FrontendUser identity) {
        this.user = identity;

        if (view != null) {
            view.setFrontendUser(identity);
        }

        log.trace("Changed identity in presenter. user={}, view={}, presenter={}", user, view, this);
    }
}
