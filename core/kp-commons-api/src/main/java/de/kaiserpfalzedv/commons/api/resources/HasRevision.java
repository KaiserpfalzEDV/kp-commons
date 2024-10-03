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
import java.time.OffsetDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * HasRevision --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.0.0  2024-09-22
 * @since 2.0.0  2023-01-06
 * 
 * @param T The type of Id of this resource (I normally use UUID).
 */
@Schema(description = "If something has a revision.")
public interface HasRevision<T extends Serializable> extends HasId<T> {
    /**
     * @return The date of the last version. It is literally the {@link #getModified()}.
     * @deprecated Will be removed with version 5.0.0 of this library.
     */
    // TODO 2024-09-27 klenkes74 Remove with 5.0.0
    @SuppressWarnings({"java:S1133","java:S1135"})
    @Schema(description = "The date and time of the current revision.")
    @Deprecated
    default OffsetDateTime getRevisioned() {
        return getModified();
    }

    @Schema(description = "The version of this resource.")
    Integer getVersion();

    @Schema(description = "The creation date of this resource.")
    OffsetDateTime getCreated();

    @Schema(description = "The date of the last modification to this resource.")
    OffsetDateTime getModified();
}
