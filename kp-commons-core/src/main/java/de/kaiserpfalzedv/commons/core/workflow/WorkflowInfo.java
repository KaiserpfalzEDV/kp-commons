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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

/**
 * WorkflowInfo -- Default workflow information of a request.
 *
 * This info should be generated on the northbound interface of a system. It should be passed to other system calls.
 * Normally as out-of-band information. So the encoding may change (HTTP-Headers, JMS properties, ...).
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
@Schema(name = "WorkflowInfo", description = "Information of a workflow call.")
public class WorkflowInfo implements Serializable {
    @Schema(
            description = "The big workflow this call belongs to.",
            required = true,
            example = "{\"name\": \"create-user\", \"id\": \"37625db0-f418-4695-9f03-ccea94234399\", \"created\": \"2022-01-04T14:22:00.000000Z\", \"ttl\": \"2022-01-04T14:25:00.000000Z\"}",
            defaultValue = "A workfow with random ID and current timestamps"
    )
    @Builder.Default
    private WorkflowDetailInfo workflow = WorkflowDetailInfo.builder().build();

    @Schema(
            description = "The action within the workflow this call belongs to.",
            required = true,
            example = "{\"name\": \"check-duplicate\", \"id\": \"81f76259-e9fa-4af2-bba7-255f7370fe41\", \"created\": \"2022-01-04T14:22:00.023000Z\", \"ttl\": \"2022-01-04T14:22:02.023000Z\"}"
    )
    @Builder.Default
    private WorkflowDetailInfo action = WorkflowDetailInfo.builder().build();

    @Schema(
            description = "The actual service call.",
            required = true,
            example = "{\"name\": \"get-user\", \"id\": \"69de33eb-6a9d-4010-9fb9-e35a4ac56eb8\", \"created\": \"2022-01-04T14:22:00.028000Z\", \"ttl\": \"2022-01-04T14:22:01.028000Z\"}"
    )
    @Builder.Default
    private WorkflowDetailInfo call = WorkflowDetailInfo.builder().build();

    @Schema(
            description = "The owner of this request. Can be an ID, a name or anything else. Please keep GDPR in mind!",
            example = "klenkes74",
            defaultValue = "Random UUID",
            nullable = true,
            minLength = 1,
            maxLength = 100
    )
    @Builder.Default
    private String user = UUID.randomUUID().toString();
}
