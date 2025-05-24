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

package de.kaiserpfalzedv.commons.users.messaging;


import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@Service
@Scope("singleton")
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class SendUserStateEventsHandler extends AbstractSendUserEventsHandler {
  
  @Inject
  public SendUserStateEventsHandler(@NotNull StreamBridge sender, @NotNull final UserEventMessagingConverter converter) {
    super(sender, converter);
  }
  
  @EventListener
  public void onUserActivated(@NotNull @Valid final UserActivatedEvent event) {
    log.entry(event);

    sendEvent("activateUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserBanned(@NotNull @Valid final UserBannedEvent event) {
    log.entry(event);

    sendEvent("banUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserCreated(@NotNull @Valid final UserCreatedEvent event) {
    log.entry(event);

    sendEvent("createUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserDeleted(@NotNull @Valid final UserDeletedEvent event) {
    log.entry(event);

    sendEvent("deleteUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserDetained(@NotNull @Valid final UserDetainedEvent event) {
    log.entry(event);

    sendEvent("detainUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserReleased(@NotNull @Valid final UserReleasedEvent event) {
    log.entry(event);

    sendEvent("releaseUser-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserRemoved(@NotNull @Valid final UserRemovedEvent event) {
    log.entry(event);

    sendEvent("removeUser-in-0", event);

    log.exit();
  }
}
