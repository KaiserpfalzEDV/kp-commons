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


import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLogoutEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@SpringBootTest(classes = SendingUserActivityEventsHandlerIT.TestConfiguration.class)
@ActiveProfiles("test")
@EnableTestBinder
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@XSlf4j
public class SendingUserActivityEventsHandlerIT {
  private final ApplicationEventPublisher bus;
  
  @Autowired
  private InputDestination input;
  
  @Autowired
  private OutputDestination output;
  
  @Autowired
  private ObjectMapper jsonMapper;
  
  @Test
  void shouldReceiveUserLoginEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserLoginEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.activity");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserLoginEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldReceiveUserLogoutEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserLogoutEvent.builder()
        .application("kp-users")
        .user(DEFAULT_USER)
        .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.activity");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserLogoutEvent.class));
    
    log.exit();
  }
  
  
  @SpringBootApplication
  @EnableUsersMessaging
  @RequiredArgsConstructor(onConstructor_ = {@Autowired})
  public static class TestConfiguration {
    @Getter(onMethod_ = @__(@Bean))
    private final ReceiveUserActivityConfig handler;
  }
  
  private static final KpUserDetails DEFAULT_USER = KpUserDetails.builder()
      .issuer("issuer")
      .subject("subject")
      
      .nameSpace("namespace")
      .name("name")
      
      .email("email@email.email")
      .discord("discord")
      
      .created(OffsetDateTime.now())
      .modified(OffsetDateTime.now())
      
      .build();
}
