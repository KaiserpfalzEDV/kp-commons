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

package de.kaiserpfalzedv.commons.api.i18n;

import jakarta.validation.constraints.NotNull;
import java.util.Locale;

/**
 * @author rlichti
 * @version 1.0.0 2021-09-08
 * @since 1.0.0 2021-09-08
 */
public interface MessageSource {
    String getMessage(@NotNull String key, @NotNull Object[] params, @NotNull Locale locale) throws NoSuchMessageException;
}
