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


import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@XSlf4j
public class SendingUserActivityEventsConfigIT {
  private final LoggingEventBus bus;
  
  @Autowired
  private OutputDestination output;
  
  @Test
  void shouldReceiveUserActivationEventWhenItIsSent() {
    log.entry();
    
    // Given
    final var event = UserLoginEvent.builder()
            .application("kp-users")
            .user(KpUserDetails.builder()
                .issuer("issuer")
                .subject("subject")
                
                .nameSpace("namespace")
                .name("name")
                
                .email("email@email.email")
                .discord("discord")
                
                .created(OffsetDateTime.now())
                .modified(OffsetDateTime.now())
                
                .build()
            )
            .build();
    
    bus.post(event);
    
    Message<byte[]> result = output.receive(100L, "sendUserLogin-out-0");
    
    assertNotNull(result);
    
    log.exit();
  }
  
  @SpringBootApplication
  @EnableUsersMessaging
  @Import({
      OutputDestination.class,
  })
  @RequiredArgsConstructor(onConstructor_ = {@Autowired})
  public static class TestConfiguration {
    @Getter(onMethod_ = @__(@Bean))
    private final SendUserActivityEventsConfig handler;
  }
}
