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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.kaiserpfalzedv.commons.api.resources.Status;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * ResourceStatus -- The state of the managed resource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@SuppressFBWarnings(value = "EI_EXPOSE_REF2", justification = "Use of lombok provided builder.")
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"observedGeneration,history"})
@Schema(name = "ResourceStatus", description = "The status of a resource.")
public class StatusImpl implements Status {
    private static final long serialVersionUID = 0L;

    @Builder.Default
    @ToString.Include
    private final Integer observedGeneration = 0;


    @Builder.Default
    @ToString.Include
    @SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
    private List<HistoryImpl> history = new ArrayList<>();

    @Override
    public StatusImpl addHistory(final String status, final String message) {
        this.getHistory().add(
                HistoryImpl.builder()
                        .status(status)
                        .timeStamp(OffsetDateTime.now(ZoneOffset.UTC))
                        .message(message)
                        .build()
        );

        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @SuppressFBWarnings(value = "CN_IDIOM_NO_SUPER_CALL", justification = "Using the lombok builder.")
    @Override
    public StatusImpl clone() {
        return this.toBuilder().build();
    }
}
