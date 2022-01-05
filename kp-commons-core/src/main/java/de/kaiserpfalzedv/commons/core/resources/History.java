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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


/**
 * A single history entry. Basic data is the timestamp, the status and the message.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Embeddable
@Builder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = History.HistoryBuilder.class)
@Schema(
        name = "History",
        description = "A single history entry of a change."
)
public class History implements Serializable, Cloneable {
    @Schema(
            name = "TimeStamp",
            description = "The timestamp of the change.",
            required = true,
            example = "2022-01-04T21:51:00.000000Z",
            defaultValue = "now"
    )
    @Builder.Default
    private final OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "Status",
            description = "The resource status after the change.",
            required = true,
            example = "Not specified",
            defaultValue = "Not specified"
    )
    @Builder.Default
    private final String status = "Not specified";

    @Schema(
            name = "Message",
            description = "The human readable description of the change.",
            nullable = true
    )
    @Builder.Default
    private final String message = null;


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public History clone() {
        return History.builder()
                .withStatus(status)
                .withTimeStamp(timeStamp)
                .withMessage(message)
                .build();
    }
}
