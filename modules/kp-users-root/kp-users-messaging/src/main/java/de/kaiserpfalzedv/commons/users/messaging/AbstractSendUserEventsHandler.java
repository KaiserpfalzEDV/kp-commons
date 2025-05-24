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


import de.kaiserpfalzedv.commons.api.events.BaseEvent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-23
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@XSlf4j
public class AbstractSendUserEventsHandler {
  private final StreamBridge sender;
  private final UserEventMessagingConverter converter;
  
  @Value("${spring.application.name:kp-users}")
  private String application = "kp-users";
  
  protected void sendEvent(@NotNull final String binding, @NotNull final BaseEvent event) {
    log.entry(binding, event);
    
    if (isLocalEvent(event)) {
      //noinspection unchecked
      Message<String> message = (Message<String>) converter.toMessage(event, converter.headers(event));
      
      if (message == null) {
        throw log.throwing(new IllegalArgumentException("Cannot convert event to message. event=%s".formatted(event)));
      }
      
      log.info("Sending event. binding={}, message={}", binding, message);
      sender.send(binding, message);
    }
    
    log.exit();
  }
  
  private boolean isLocalEvent(final BaseEvent event) {
    return application.equals(event.getApplication());
  }
}
