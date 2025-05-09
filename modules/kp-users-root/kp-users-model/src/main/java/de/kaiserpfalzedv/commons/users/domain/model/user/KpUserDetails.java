/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.users.domain.model.user;

import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.events.arbitation.UserBannedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.arbitation.UserDetainedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.arbitation.UserReleasedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserDeletedEvent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString(of = {"id", "nameSpace", "name"})
@EqualsAndHashCode(of = {"id"})
@XSlf4j
public class KpUserDetails implements User {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    
    @Builder.Default
    private OffsetDateTime created = OffsetDateTime.now(Clock.systemUTC());
    private OffsetDateTime modified;
    private OffsetDateTime deleted;
    
    private Duration detainmentDuration;
    private OffsetDateTime detainedTill;
    
    private OffsetDateTime bannedOn;

    @Builder.Default
    private String nameSpace = "./.";
    private String name;
    
    private String issuer;
    private String subject;
    
    @Builder.Default
    private final Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    
    
    @Override
    public KpUserDetails detain(@NotNull EventBus bus, @Min(1) @Max(1095) long days) {
        log.entry(bus, days);
        
        detainmentDuration = Duration.ofDays(days);
        
        detainedTill = LocalDate.now()
            .atStartOfDay(ZoneId.of("UTC"))
            .plusDays(1 + days) // today end of day (1) + days
            .toOffsetDateTime();
        
        bus.post(UserDetainedEvent.builder().user(this).days(days).build());
        
        return log.exit(this);
    }
    
    @Override
    public KpUserDetails release(@NotNull EventBus bus) {
        log.entry(bus);
        
        detainmentDuration = null;
        detainedTill = null;
        
        bus.post(UserReleasedEvent.builder().user(this).build());
        
        return log.exit(this);
    }
    
    @Override
    public boolean isBanned() {
        return bannedOn != null;
    }
    
    @Override
    public KpUserDetails ban(@NotNull EventBus bus) {
        log.entry(bus);

        bannedOn = OffsetDateTime.now(Clock.systemUTC());
        
        bus.post(UserBannedEvent.builder().user(this).timestamp(bannedOn).build());
        
        return log.exit(this);
    }
    
    @Override
    public KpUserDetails unban(@NotNull EventBus bus) {
        log.entry(bus);
        
        bannedOn = null;
        
        bus.post(UserReleasedEvent.builder().user(this).build());
        
        return log.exit(this);
    }
    
    @Override
    public KpUserDetails delete(@NotNull EventBus bus) {
        log.entry(bus);
        
        this.deleted = OffsetDateTime.now(Clock.systemUTC());
        
        bus.post(UserDeletedEvent.builder().user(this).timestamp(deleted).build());
        
        return log.exit(this);
    }
    
    @Override
    public KpUserDetails undelete(@NotNull EventBus bus) {
        log.entry(bus);
        
        this.deleted = null;
        
        bus.post(UserReleasedEvent.builder().user(this).build());
        
        return log.exit(this);
    }
    
    @Override
    public void eraseCredentials() {
        // nothing to do, there are no credentials anywhere ...
    }
}
