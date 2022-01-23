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

package de.kaiserpfalzedv.commons.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.resources.DefaultResourceSpec;
import de.kaiserpfalzedv.commons.core.resources.ResourcePointer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0 2021-02-06
 */
@SuppressWarnings("unused")
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(name = "GameData", description = "A game session data.")
public class EventData extends DefaultResourceSpec {
    @Schema(description = "An Event may have sub events (like a convention may have tracks or multiple sessions.", nullable = true, minItems = 0)
    @Builder.Default
    private final Set<ResourcePointer> subEvents = new HashSet<>();

    @Schema(description = "The location of the event.", nullable = true)
    @NonNull
    private final ResourcePointer location;

    @Schema(description = "The start of the event. Includes also the timezone this event takes place.", required = true)
    @NonNull
    private final OffsetDateTime startsAt;

    @Schema(description = "The duration of the event.", nullable = true)
    @NonNull
    private final Duration duration;

    @JsonIgnore
    @BsonIgnore
    public boolean hasSessions() {
        return subEvents != null && !subEvents.isEmpty();
    }

    @JsonIgnore
    @BsonIgnore
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(startsAt.toZonedDateTime().getZone());
    }
}
