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

package de.kaiserpfalzedv.commons.users.domain.model.user.events;


import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLogoutEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikeys.events.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikeys.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserBannedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserDetainedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitation.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserReleasedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserActivatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserDeletedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserRemovedEvent;

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
