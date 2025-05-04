/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.kaiserpfalzedv.commons.users.domain.model.events;


import de.kaiserpfalzedv.commons.users.domain.model.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.activity.UserLogoutEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.apikey.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.apikey.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserBannedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserDetainedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.arbitation.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserReleasedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserActivatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserDeletedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.UserRemovedEvent;

/**
 * This is the interface for the SCSes to implement to react on user events according to their own needs.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 12.04.25
 */
public interface UserEventsHandler {
  void event(UserActivatedEvent event);
  void event(UserCreatedEvent event);
  void event(UserDeletedEvent event);
  void event(UserRemovedEvent event);
  
  void event(UserBannedEvent event);
  void event(UserDetainedEvent event);
  void event(UserPetitionedEvent event);
  void event(UserReleasedEvent event);
  
  void event(UserLoginEvent event);
  void event(UserLogoutEvent event);
  
  void event(ApiKeyCreatedEvent event);
  void event(ApiKeyRevokedEvent event);
}
