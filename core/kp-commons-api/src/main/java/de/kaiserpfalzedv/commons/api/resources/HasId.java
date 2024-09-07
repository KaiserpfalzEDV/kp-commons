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

/**
 * HasId --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.1.0  2022-01-16
 * @since 0.1.0  2021-04-18
 */
public interface HasId extends Serializable {
    int MIN_LENGTH = 1;
    int MAX_LENGTH = 20;
    String VALID_ID_PATTERN = "^[0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}";
    String VALID_ID_PATTERN_MSG = "The ID pattern must match '" + VALID_ID_PATTERN + "'";
    String VALID_ID_LENGTH_MSG = "The ID must be between one and 20 digits long.";
    String VALID_ID_EXAMPLE = "4324324";

    @Schema(
            name = "id",
            description = "The id of a resource.",
            pattern = VALID_ID_PATTERN,
            minLength = MIN_LENGTH,
            maxLength = MAX_LENGTH,
            example = VALID_ID_EXAMPLE
    )
    Long getId();
}
