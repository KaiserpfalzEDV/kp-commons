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

import de.kaiserpfalzedv.commons.vaadin.i18n.AutoPageTitle;
import de.kaiserpfalzedv.commons.vaadin.users.FrontendUser;

import javax.validation.constraints.NotNull;

/**
 * Basic Presenter -- Common API to every View.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
public interface BasicView<T> extends AutoPageTitle {
    /**
     * The data to handle in the view.
     *
     * @param data The data to be displayed/edited
     */
    void setData(T data);


    /**
     * Updates the logged in user.
     *
     * @param identity
     */
    void setFrontendUser(@NotNull final FrontendUser identity);
}
