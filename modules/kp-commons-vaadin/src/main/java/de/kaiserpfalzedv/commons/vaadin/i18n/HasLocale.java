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

package de.kaiserpfalzedv.commons.vaadin.i18n;

import java.util.Locale;

/**
 * HasLocale -- This resource offers a locale information.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-09
 */
public interface HasLocale {
    Locale getLocale();
}
