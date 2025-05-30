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


import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLogoutEvent;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Handles user events and sends them to the appropriate topic.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class ReceiveUserActivityConfig {
  private final ApplicationEventPublisher bus;
  
  @Value("${spring.application.name:kp-users}")
  private String application = "kp-users";
  
  
  @Bean
  Consumer<UserLoginEvent> loginUser() {
    return event -> {
      log.entry(event);
      
      if (isExternalEvent(event)) {
        log.info("Sending event locally. event={}", event);
        
        bus.publishEvent(event);
      }
      log.exit();
    };
  }
  
  
  @Bean
  Consumer<UserLogoutEvent> logoutUser() {
    return event -> {
      log.entry(event);
      
      if (isExternalEvent(event)) {
        log.info("Sending event locally. event={}", event);
        bus.publishEvent(event);
      }
      
      log.exit();
    };
  }
  
  private boolean isExternalEvent(final UserBaseEvent event) {
    return !application.equals(event.getApplication());
  }
}
