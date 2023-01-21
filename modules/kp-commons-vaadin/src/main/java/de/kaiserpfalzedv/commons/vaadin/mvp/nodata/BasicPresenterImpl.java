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

package de.kaiserpfalzedv.commons.vaadin.mvp.nodata;

import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * <p>BasicPresenterImpl -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-21
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@Slf4j
public abstract class BasicPresenterImpl implements BasicPresenter {
    @ToString.Include
    protected BasicView view;
    @ToString.Include
    protected BasicForm form;
    @ToString.Include
    protected final FrontendUser user;

    public abstract void reset();

    abstract public void close();

    public <V extends BasicView> void setView(@NotNull V view) {
        this.view = view;

        if (user != null) {
            view.setFrontendUser(user);
        }

       log.trace("Added view to presenter. presenter={}, view={}, form={}", this, this.view, form);
    }
}
