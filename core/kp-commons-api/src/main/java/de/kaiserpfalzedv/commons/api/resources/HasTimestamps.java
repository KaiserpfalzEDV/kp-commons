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

import java.time.OffsetDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * HasTimestamps --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 3.3.3-3  2024-09-08
 * @since 3.3.3-3    2024-09-08
 */
public interface HasTimestamps {
    String VALID_PATTERN = "^[0-9]{4}(-[0-9]{2}){2}T([0-9]{2}:){2}[0-9]{2}.[0-9]{6}+[0-9]{2}:[0-9]{2}$";
    String VALID_PATTERN_MSG = "The timestamp must match the pattern '" + VALID_PATTERN + "'";
    int VALID_LENGTH = 32;
    String VALID_LENGTH_MSG = "The timestamp must be exactly 32 characters long.";
    String VALID_EXAMPLE = "2022-01-04T21:51:00.000000+01:00";

    @Schema(
            name = "created",
            description = "The creation date of this resource.",
            maxLength = HasTimestamps.VALID_LENGTH,
            pattern = HasTimestamps.VALID_PATTERN
    )
    @NotNull
    OffsetDateTime getCreated();

    @Schema(
            name = "modified",
            description = "The modified date of this resource.",
            maxLength = HasTimestamps.VALID_LENGTH,
            pattern = HasTimestamps.VALID_PATTERN
    )
    @NotNull
    OffsetDateTime getModified();

    @Schema(
            name = "deleted",
            description = "The deletion date of this resource.",
            maxLength = HasTimestamps.VALID_LENGTH,
            pattern = HasTimestamps.VALID_PATTERN
    )
    OffsetDateTime getDeleted();
}
