/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.users.store.model.user;

import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleAddedToUserEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleRemovedFromUserEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import de.kaiserpfalzedv.commons.users.store.model.apikey.ApiKeyJPA;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

import java.time.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The player
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "USERS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"ISSUER", "SUBJECT"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@XSlf4j
public class UserJPA extends AbstractRevisionedJPAEntity<UUID> implements User {
    @Nullable
    @Column(name = "DETAINED_DURATION")
    private Duration detainmentDuration;
    
    @Nullable
    @Column(name = "DETAINED_TILL")
    private OffsetDateTime detainedTill;
    
    @NotNull
    @Column(name = "BANNED", nullable = false)
    private OffsetDateTime bannedOn;
    
    @NotNull
    @NotBlank
    @Column(name = "ISSUER", nullable = false)
    private String issuer;
    
    @NotNull
    @NotBlank
    @Column(name = "SUBJECT", nullable = false)
    private String subject;
    
    /** The namespace this player is registered for. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", nullable = false, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the player. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", nullable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;
    
    @Nullable
    @Email
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(100)")
    private String email;
    
    @Nullable
    @Column(name = "PHONE", columnDefinition = "VARCHAR(100)")
    private String phone;
    
    @Nullable
    @Column(name = "DISCORD", columnDefinition = "VARCHAR(100)")
    private String discord;
    
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USERS_ROLES",
        joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )
    @Builder.Default
    private Set<RoleJPA> authorities = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Builder.Default
    private Set<ApiKeyJPA> apiKeys = new HashSet<>();
    
    
    @Override
    public UserJPA detain(@NotNull EventBus bus, @Min(1)@Max(1095) long days) {
        log.entry(days);
        
        detainmentDuration = Duration.ofDays(days);
        
        detainedTill = LocalDate.now()
            .atStartOfDay(ZoneId.of("UTC"))
            .plusDays(1+ days) // today end of day (1) + days
            .toOffsetDateTime();
        
        bus.post(UserDetainedEvent.builder().user(this).days(days).build());
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA release(@NotNull EventBus bus) {
        log.entry();
        
        detainmentDuration = null;
        detainedTill = null;
        bannedOn = null;
        
        bus.post(UserReleasedEvent.builder().user(this).build());
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA ban(@NotNull EventBus bus) {
        log.entry();
        
        this.bannedOn = OffsetDateTime.now();
        
        bus.post(UserBannedEvent.builder().user(this).timestamp(bannedOn).build());
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA delete(@NotNull EventBus bus) {
        log.entry();
        
        this.deleted = OffsetDateTime.now(Clock.systemUTC());
        
        bus.post(UserDeletedEvent.builder().user(this).timestamp(deleted).build());
        
        return log.exit(this);
    }
    
    @Override
    public UserJPA undelete(@NotNull EventBus bus) {
        log.entry();
        
        this.deleted = null;
        
        bus.post(UserActivatedEvent.builder().user(this).build());
        
        return log.exit(this);
    }

    
    @Override
    public void eraseCredentials() {
        // do nothing. We don't have credentials ...
    }
    
    public UserJPA addRole(@NotNull EventBus bus, @NotNull RoleJPA role) {
        log.entry(role);
        
        if (! authorities.contains(role)) {
            authorities.add(role);
            
            bus.post(RoleAddedToUserEvent.builder().user(this).role(role).build());
        }
        
        return log.exit(this);
    }
    
    public UserJPA removeRole(@NotNull EventBus bus, @NotNull RoleJPA role) {
        log.entry(role);
        
        if (authorities.contains(role)) {
            authorities.remove(role);
            
            bus.post(RoleRemovedFromUserEvent.builder().user(this).role(role).build());
        }
        
        return log.exit(this);
    }
}
