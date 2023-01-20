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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * HasApiVersion -- The object has an api version.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2022-01-16
 * @since 2.0.2  2022-01-16
 */
public interface HasApiVersion {
    String VALID_VERSION_PATTERN = "^[a-zA-Z]([a-zA-Z\\d]{1,9})?$";
    String VALID_VERSION_PATTERN_MSG = "The version must match the pattern '" + VALID_VERSION_PATTERN + "'.";
    String VALID_VERSION_EXAMPLE = "v1";

    int VALID_VERSION_MIN_LENGTH = 1;
    int VALID_VERSION_MAX_LENGTH = 10;
    String VALID_VERSION_LENGTH_MSG = "The length of an api version must be between "
            + VALID_VERSION_MIN_LENGTH + " and " + VALID_VERSION_MAX_LENGTH + " characters.";

    @Schema(
            name = "apiVersion",
            description = "The version of a resource.",
            example = "v1",
            defaultValue = "v1",
            pattern = VALID_VERSION_PATTERN,
            minLength = VALID_VERSION_MIN_LENGTH,
            maxLength = VALID_VERSION_MAX_LENGTH
    )
    @NotBlank
    @Size(min = VALID_VERSION_MIN_LENGTH, max = VALID_VERSION_MAX_LENGTH, message = "The API Version is either too long or too short.")
    @Pattern(regexp = VALID_VERSION_PATTERN, message = "The api version does not match the validation pattern.")
    String getApiVersion();
}
