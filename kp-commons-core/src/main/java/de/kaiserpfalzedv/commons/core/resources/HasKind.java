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

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static de.kaiserpfalzedv.commons.core.resources.HasName.*;

/**
 * HasKind -- The object has a kind.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.1.0  2022-01-16
 * @since 2.0.2  2022-01-16
 */
public interface HasKind {
    @Schema(
            name = "kind",
            description = "The kind of a resource.",
            example = VALID_NAME_EXAMPLE,
            pattern = VALID_NAME_PATTERN,
            minLength = VALID_NAME_MIN_LENGTH,
            maxLength = VALID_NAME_MAX_LENGTH
    )
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH, message = VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = VALID_NAME_PATTERN, message = VALID_NAME_PATTERN_MSG)
    String getKind();
}
