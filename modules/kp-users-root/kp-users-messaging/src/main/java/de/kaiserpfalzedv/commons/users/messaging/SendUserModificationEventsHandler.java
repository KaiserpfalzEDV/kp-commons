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


import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
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
public class SendUserModificationEventsHandler extends AbstractSendUserEventsHandler {
  
  @Inject
  public SendUserModificationEventsHandler(@NotNull StreamBridge sender, @NotNull final UserEventMessagingConverter converter) {
    super(sender, converter);
  }
  
  @EventListener
  public void onRoleAddedToUser(@NotNull @Valid final RoleAddedToUserEvent event) {
    log.entry(event);

    sendEvent("addingRole-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onRoleRemovedFromUser(@NotNull @Valid final RoleRemovedFromUserEvent event) {
    log.entry(event);

    sendEvent("removeRole-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onModifyDiscord(@Valid final UserDiscordModificationEvent event) {
    log.entry(event);

    sendEvent("modifyDiscord-in-0", event);

    log.exit();
  }
  
  @EventListener
  public void onUserEmailModification(@Valid final UserEmailModificationEvent event) {
    log.entry(event);

    sendEvent("modifyEmail-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onUserNameModification(@Valid final UserNameModificationEvent event) {
    log.entry(event);

    sendEvent("modifyName-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onUserNamespaceModification(@Valid final UserNamespaceModificationEvent event) {
    log.entry(event);

    sendEvent("modifyNamespace-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onUserNamespaceAndNameModification(@Valid final UserNamespaceAndNameModificationEvent event) {
    log.entry(event);

    sendEvent("modifyNamespaceAndName-in-0", event);

    log.exit();
  }
  
  
  @EventListener
  public void onUserSubjectModification(@Valid final UserSubjectModificationEvent event) {
    log.entry(event);

    sendEvent("modifySubject-in-0", event);

    log.exit();
  }
}
