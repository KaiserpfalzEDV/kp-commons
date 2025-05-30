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

package de.kaiserpfalzedv.commons.users.store.model.apikey;

import de.kaiserpfalzedv.commons.jpa.AbstractJPAEntity;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKey;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The APIKEY for a player.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "APIKEYS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}, name = "APIKEYS_ID_UK")
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
public class ApiKeyJPA extends AbstractJPAEntity<UUID> implements ApiKey {
    /** The namespace this player is registered for. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", nullable = false, updatable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "USER", nullable = false, updatable = false, referencedColumnName = "ID", foreignKey = @ForeignKey(name = "APIKEYS_USER_FK"))
    private UserJPA user;
    
    @NotNull
    @Column(name = "EXPIRATION")
    @ToString.Include
    private OffsetDateTime expiration;
}
