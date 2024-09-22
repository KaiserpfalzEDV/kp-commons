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

package de.kaiserpfalzedv.commons.api.resources;

import java.io.Serializable;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * <p>Status -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface Status extends Serializable, Cloneable {
    /**
     * Adds a new history entry.
     *
     * @param status  The status of this entry.
     * @param message The generic message for this history entry.
     * @return TRUE if the history could be added.
     */
    Status addHistory(String status, String message);

    @Schema(
            name = "observedGeneration",
            description = "The generation of this resource which is observed.",
            required = true,
            defaultValue = "0",
            minimum = "0",
            maxItems = Integer.MAX_VALUE
    )
    @Min(value = 0, message = "The generation must be at least 0.")
    @Max(value = Integer.MAX_VALUE, message = "The generation must not be bigger than " + Integer.MAX_VALUE + ".")
    Integer getObservedGeneration();

    @Schema(
            name = "history",
            description = "A list of changes of the resource status.",
            nullable = true,
            minItems = 0
    )
    @Size
    <T extends History> List<T> getHistory();
}
