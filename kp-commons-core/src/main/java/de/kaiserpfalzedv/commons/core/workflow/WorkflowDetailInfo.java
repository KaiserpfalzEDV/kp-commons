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

package de.kaiserpfalzedv.commons.core.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

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
@Slf4j
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(name = "WorkflowDetailInfo", description = "Identity and timing data of workflows, actions and calls.")
public class WorkflowDetailInfo implements Serializable {
    @Schema(
            description = "A user understandable name.",
            example = "create-user",
            nullable = true,
            minLength = 1,
            maxLength = 100
    )
    private String name;

    @Schema(
            description = "The ID",
            example = "b9fa12c5-16b6-4272-b504-1b4177815442",
            defaultValue = "Random UUID",
            required = true,
            minLength = 1,
            maxLength = 100
    )
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Schema(
            description = "The creation time.",
            example = "2022-01-04T14:07:00.1312321Z",
            defaultValue = "now",
            required = true
    )
    @Builder.Default
    private OffsetDateTime created = OffsetDateTime.now(ZoneId.of("UTC"));


    @Schema(
            description = "The time the result does not matter any more. Services may stop working on the answer and return an error instead.",
            example = "2023-01-01T01:00:00.000000Z",
            defaultValue = "10 Years in future",
            required = true
    )
    @Builder.Default
    private OffsetDateTime ttl = OffsetDateTime.now(ZoneId.of("UTC")).plusYears(10);


    @Schema(
            description = "The response channel to use. Normally an URI containing the response channel for asynchronous answers.",
            example = "https://invalid.invalid/invalid",
            nullable = true
    )
    private String responseChannel;
}
