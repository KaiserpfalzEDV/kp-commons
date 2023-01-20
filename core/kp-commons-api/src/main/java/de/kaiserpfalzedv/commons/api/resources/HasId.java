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

package de.kaiserpfalzedv.commons.api.resources;

import java.io.Serializable;
import java.util.UUID;

/**
 * HasId --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.1.0  2022-01-16
 * @since 0.1.0  2021-04-18
 */
public interface HasId extends Serializable {
    String VALID_UUID_PATTERN = "^[a-zA-Z0-9]{8}-([a-zA-Z0-9]{4}-){3}[a-zA-Z0-9]{12}$";
    String VALID_UUID_PATTERN_MSG = "The UUID pattern must match '" + VALID_UUID_PATTERN + "'";
    int VALID_UUID_LENGTH = 36;
    String VALID_UUID_LENGTH_MSG = "The UUID must be exactely 36 characters long.";
    String VALID_UUID_EXAMPLE = "caae022d-5728-4cb2-9245-b8c1ea03e380";

    UUID getId();
}
