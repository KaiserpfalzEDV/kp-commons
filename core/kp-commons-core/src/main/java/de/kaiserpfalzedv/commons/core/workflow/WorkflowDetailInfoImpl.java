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

package de.kaiserpfalzedv.commons.core.workflow;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.kaiserpfalzedv.commons.api.workflow.WorkflowDetailInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * WorkflowDetailInfo -- The details of a workflow, action and call.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-04
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(
        name = "WorkflowDetailInfo",
        description = "Identity and timing data of workflows, actions and calls.",
        required = true,
        defaultValue = "A workfow with random ID and current timestamps"
)
public class WorkflowDetailInfoImpl implements WorkflowDetailInfo {
    private static final long serialVersionUID = 0L;

    @Schema(
            description = "A user understandable name.",
            nullable = true,
            minLength = 1,
            maxLength = 100
    )
    @NotNull
    private String name;

    @Schema(
            description = "The ID",
            defaultValue = "Random UUID",
            required = true,
            minLength = 1,
            maxLength = 100
    )
    @NotNull
    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Schema(
            description = "The creation time.",
            defaultValue = "now",
            required = true
    )
    @NotNull
    @Builder.Default
    private final OffsetDateTime created = OffsetDateTime.now(ZoneId.of("UTC"));


    @Schema(
            description = "The time the result does not matter any more. Services may stop working on the answer and return an error instead.",
            defaultValue = "10 Years in future",
            required = true
    )
    @NotNull
    @Builder.Default
    private final OffsetDateTime ttl = OffsetDateTime.now(ZoneId.of("UTC")).plusYears(10);


    @Schema(
            description = "The response channel to use. Normally an URI containing the response channel for asynchronous answers.",
            nullable = true
    )
    @Nullable
    private String responseChannel;
}
