/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.core.resources;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * HasName -- The object has a name.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2022-01-16
 * @since 0.1.0  2021-04-18
 */
public interface HasName {
    String VALID_NAME_PATTERN = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$";
    String VALID_NAME_PATTERN_MSG = "The string must match the pattern '" + VALID_NAME_PATTERN + "'";

    int VALID_NAME_MIN_LENGTH = 3;
    int VALID_NAME_MAX_LENGTH = 100;
    String VALID_NAME_LENGTH_MSG = "The length of the string must be between "
            + VALID_NAME_MIN_LENGTH + " and " + VALID_NAME_MAX_LENGTH + " characters long.";

    String VALID_NAME_EXAMPLE = "valid-name";

    @Schema(
            name = "name",
            description = "The name of a resource.",
            pattern = VALID_NAME_PATTERN,
            minLength = VALID_NAME_MIN_LENGTH,
            maxLength = VALID_NAME_MAX_LENGTH,
            example = VALID_NAME_EXAMPLE
    )
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH, message = VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = VALID_NAME_PATTERN, message = VALID_NAME_PATTERN_MSG)
    String getName();
}
