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

package de.kaiserpfalzedv.commons.vaadin.mvp.data;

import de.kaiserpfalzedv.commons.vaadin.mvp.nodata.BasicPresenterImpl;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public abstract class BasicDataPresenterImpl<T extends Serializable> extends BasicPresenterImpl implements BasicDataPresenter<T> {

    @Getter(AccessLevel.PUBLIC)
    @ToString.Include
    protected T data;

    @Inject
    public BasicDataPresenterImpl(@SuppressWarnings("CdiInjectionPointsInspection") @NotNull final FrontendUser user) {
        super(user);
    }

    @Override
    abstract public void loadId(@NotNull UUID id);

    abstract public void save();
    abstract public void delete();

    @Override
    public void setData(@NotNull T data) {
        this.data = data;
        log.trace("Data reset in presenter. presenter={}, data={}", this, data);

        reset();
    }
}
