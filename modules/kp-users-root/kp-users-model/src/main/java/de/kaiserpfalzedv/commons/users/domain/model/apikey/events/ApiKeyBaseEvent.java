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

package de.kaiserpfalzedv.commons.users.domain.model.apikey.events;


import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKey;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserBaseEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.OffsetDateTime;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class ApiKeyBaseEvent extends UserBaseEvent {
  private final ApiKey apiKey;
  
  @Override
  public User getUser() {
    return getApiKey().getUser();
  }
  
  @Override
  public Object[] getI18nData() {
    return new Object[]{
        getTimestamp(),
        getApplication(),
        getApiKey().getNameSpace(),
        getApiKey().getName(),
        getApiKey().getExpiration(),
        Duration.between(OffsetDateTime.now(), getApiKey().getExpiration()),
        getUser().getId(),
        getUser().getNameSpace(),
        getUser().getName()
    };
  }
}
