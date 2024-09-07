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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * HasUid --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 3.3.3-3  2024-09-08
 * @since 3.3.3-3    2024-09-08
 */
public interface HasUid extends Serializable {
    public final String VALID_UID_MSG = "The ID must be a valid UUID.";
    public final String VALID_UID_PATTERN = "[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}";

    @Schema(
            name = "id",
            description = "The id of a resource.",
            pattern = VALID_UID_PATTERN
    )
    @NotNull
    @Pattern(regexp = VALID_UID_PATTERN, message = VALID_UID_MSG)
    UUID getUid();
}
