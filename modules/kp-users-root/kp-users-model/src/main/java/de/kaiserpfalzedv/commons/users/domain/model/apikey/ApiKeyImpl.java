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

package de.kaiserpfalzedv.commons.users.domain.model.apikey;

import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString(of = {"id", "nameSpace", "created", "expiration"})
@EqualsAndHashCode(of = {"id"})
@XSlf4j
public class ApiKeyImpl implements ApiKey {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Builder.Default
    private OffsetDateTime created = OffsetDateTime.now(Clock.systemUTC());
    private OffsetDateTime modified;
    private OffsetDateTime deleted;

    @Builder.Default
    private String nameSpace = "./.";

    /**
     * The expiration time of this API key. Defaults to 1 year from now.
     */
    @Builder.Default
    private OffsetDateTime expiration = OffsetDateTime.now(Clock.systemUTC()).plusMonths(12);
    
    @NotNull @Valid
    private KpUserDetails user;
}
