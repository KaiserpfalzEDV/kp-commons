/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * ResourceStatus -- The state of the managed resource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"observedGeneration,history"})
@Schema(name = "ResourceStatus", description = "The status of a resource.")
public class Status implements Serializable, Cloneable {
    @Schema(
            name = "observedGeneration",
            description = "The generation of this resource which is observed.",
            required = true,
            example = "0",
            defaultValue = "0",
            minimum = "0",
            maxItems = Integer.MAX_VALUE
    )
    @Builder.Default
    @ToString.Include
    @Min(value = 0, message = "The generation must be at least 0.")
    @Max(value = Integer.MAX_VALUE, message = "The generation must not be bigger than " + Integer.MAX_VALUE + ".")
    Integer observedGeneration = 0;

    @Schema(
            name = "history",
            description = "A list of changes of the resource status.",
            nullable = true,
            minItems = 0
    )
    @Builder.Default
    @ToString.Include
    List<History> history = new ArrayList<>();

    /**
     * Adds a new history entry.
     *
     * @param status  The status of this entry.
     * @param message The generic message for this history entry.
     * @return TRUE if the history could be added.
     */
    @SuppressWarnings("unused")
    public Status addHistory(final String status, final String message) {
        getHistory().add(
                History.builder()
                        .status(status)
                        .timeStamp(OffsetDateTime.now(ZoneOffset.UTC))
                        .message(message)
                        .build()
        );

        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Status clone() {
        return toBuilder().build();
    }
}
