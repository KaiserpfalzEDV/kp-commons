/*
 * Copyright (c) 2025.  Kaiserpfalz EDV-Service, Roland T. Lichti
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
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.kaiserpfalzedv.commons.users.domain.model.events.apikey;


import de.kaiserpfalzedv.commons.users.domain.model.apikeys.ApiKey;
import de.kaiserpfalzedv.commons.users.domain.model.events.UserBaseEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 21.04.25
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ApiKeyRevokedEvent extends UserBaseEvent {
  private final String i18nKey = "user.api-key.revoked";

  private final ApiKey apiKey;
}
