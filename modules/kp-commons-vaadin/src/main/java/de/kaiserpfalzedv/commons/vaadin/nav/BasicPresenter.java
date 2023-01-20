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

import de.kaiserpfalzedv.commons.vaadin.i18n.HasLocale;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;
import de.kaiserpfalzedv.commons.vaadin.users.HasUser;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

/**
 * Basic Presenter -- Common API to every Presenter.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
public interface BasicPresenter<T extends Serializable> extends HasUser, HasLocale {
    /**
     * Inserts the data for the view.
     *
     * @param data The data to be presented
     */
    public void setData(@NotNull final T data);

    /**
     * Retrieves the data from the view.
     *
     * @return The data from the view.
     */
    public T getData();

    /**
     * Inserts the view this presenter works on.
     *
     * @param view The basic view of this presenter.
     */
    public <V extends BasicView<T>> void setView(@NotNull final V view);

    /**
     * Reads the view this presenter works on.
     */
    public <V extends BasicView<T>> V getView();

    public <F extends BasicDataForm<T>> void setForm(@NotNull final F form);

    public <F extends BasicDataForm<T>> F getForm();

    /**
     * Updates the logged in user.
     *
     * @param identity
     */
    public void setFrontendUser(@NotNull final FrontendUser identity);

    default Locale getLocale() {
        return getUser().getLocale();
    }

    public void loadId(@NotNull final UUID id) throws UnsupportedOperationException;

    public void save();
    public void delete();
    public void reset();
    public void close();
}
