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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * HasId --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.1.0  2022-01-16
 * @since 0.1.0  2021-04-18
 */
public interface HasId extends Serializable {
    int MIN = 1;
    long MAX = Long.MAX_VALUE;
    String VALID_ID_LENGTH_MSG = "The ID must be between " + MIN + " and " + MAX + ".";
    String VALID_ID_EXAMPLE = "4324324";

    @Schema(
            name = "id",
            description = "The id of a resource.",
            minimum = "1",
            maximum = "9223372036854775807",
            example = VALID_ID_EXAMPLE
    )
    @NotNull
    @Min(value = MIN, message = VALID_ID_LENGTH_MSG)
    @Max(value = MAX, message = VALID_ID_LENGTH_MSG)
    Long getId();
}
