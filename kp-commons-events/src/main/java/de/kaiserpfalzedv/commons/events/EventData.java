/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.resources.DefaultResourceSpec;
import de.kaiserpfalzedv.commons.core.resources.ResourcePointer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.beans.Transient;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0 2021-02-06
 */
@SuppressWarnings("unused")
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = EventData.EventDataBuilder.class)
@Schema(name = "GameData", description = "A game session data.")
public class EventData extends DefaultResourceSpec {
    public static String CAMPAIGN = "campaign";
    public static String GAME_GM = "game.gm";
    public static String GAME_PLAYERS = "game.players";
    public static String DISCORD_GUILD = "discord.guild";
    public static String DISCORD_CHANNEL = "discord.channel";

    public static String[] STRUCTURED_PROPERTIES = {
            CAMPAIGN,
            GAME_GM,
            GAME_PLAYERS,
            DISCORD_GUILD,
            DISCORD_CHANNEL
    };

    @Schema(description = "An Event may have sub events (like a convention may have tracks or multiple sessions.", nullable = true, minItems = 0)
    @Singular
    private final Set<ResourcePointer> subEvents;

    @Schema(description = "The location of the event.", nullable = true)
    private final ResourcePointer location;

    @Schema(description = "The start of the event. Includes also the timezone this event takes place.", required = true)
    private final OffsetDateTime startsAt;

    @Schema(description = "The duration of the event.", nullable = true)
    private final Duration duration;

    @Override
    public String[] getDefaultProperties() {
        return STRUCTURED_PROPERTIES;
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public boolean hasSessions() {
        return subEvents != null && !subEvents.isEmpty();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(startsAt.toZonedDateTime().getZone());
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getCampaign() {
        return getResourcePointer(CAMPAIGN);
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getGameMaster() {
        return getResourcePointer(GAME_GM);
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public List<ResourcePointer> getPlayers() {
        return getResourcePointers(GAME_PLAYERS);
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getDiscordChannel() {
        return getResourcePointer(DISCORD_CHANNEL);
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getDiscordGuild() {
        return getResourcePointer(DISCORD_GUILD);
    }
}
