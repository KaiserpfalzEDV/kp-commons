/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
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
@Embeddable
@Builder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Status.StatusBuilder.class)
@JsonPropertyOrder({"observedGeneration,history"})
@Schema(name = "ResourceStatus", description = "The status of a resource.")
public class Status implements Serializable, Cloneable {
    @Schema(
            name = "observedGeneration",
            description = "The generation of this resource which is observed.",
            required = true,
            example = "0",
            defaultValue = "0",
            minimum = "0"
    )
    @Builder.Default
    Integer observedGeneration = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name="HISTORY")
    @CollectionTable(name="HISTORY", joinColumns=@JoinColumn(name="ID"))
    @Schema(
            name = "history",
            description = "A list of changes of the resource status.",
            nullable = true,
            minItems = 0
    )
    @Builder.Default
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
                        .withStatus(status)
                        .withTimeStamp(OffsetDateTime.now(ZoneOffset.UTC))
                        .withMessage(message)
                        .build()
        );

        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Status clone() {
        return Status.builder()
                .withObservedGeneration(observedGeneration)
                .withHistory(history)
                .build();
    }
}
