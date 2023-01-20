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

package de.kaiserpfalzedv.commons.core.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
public class Status implements de.kaiserpfalzedv.commons.api.resources.Status {

    @Builder.Default
    @ToString.Include
    private Integer observedGeneration = 0;


    @Builder.Default
    @ToString.Include
    List<History> history = new ArrayList<>();

    @Override
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
