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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.api.TimeStampPattern;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
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
            defaultValue = "now",
            example = TimeStampPattern.VALID_EXAMPLE,
            pattern = TimeStampPattern.VALID_PATTERN,
            minLength = TimeStampPattern.VALID_LENGTH,
            maxLength = TimeStampPattern.VALID_LENGTH
    )
    @Builder.Default
    @Length(
            min = TimeStampPattern.VALID_LENGTH,
            max = TimeStampPattern.VALID_LENGTH,
            message = TimeStampPattern.VALID_LENGTH_MSG
    )
    @Pattern(regexp = TimeStampPattern.VALID_PATTERN, message = TimeStampPattern.VALID_PATTERN_MSG)
    private final OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "Status",
            description = "The resource status after the change.",
            required = true,
            defaultValue = "not-specified",
            example = HasName.VALID_NAME_EXAMPLE,
            pattern = HasName.VALID_NAME_PATTERN,
            minLength = HasName.VALID_NAME_MIN_LENGTH,
            maxLength = HasName.VALID_NAME_MAX_LENGTH
    )
    @Builder.Default
    @Length(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
    private final String status = "not-specified";

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
        return toBuilder().build();
    }
}
