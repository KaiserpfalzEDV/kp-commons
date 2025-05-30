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
import de.kaiserpfalzedv.commons.users.domain.model.role.KpRole;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
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
@SpringBootTest(classes = SendingUserModificationEventsHandlerIT.TestConfiguration.class)
@ActiveProfiles("test")
@EnableTestBinder
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@XSlf4j
public class SendingUserModificationEventsHandlerIT {
  private final ApplicationEventPublisher bus;
  
  @Autowired
  private InputDestination input;
  
  @Autowired
  private OutputDestination output;
  
  @Autowired
  private ObjectMapper jsonMapper;
  
  
  @Test
  void shouldReceiveUserAddedRoleEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = RoleAddedToUserEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .role(DEFAULT_ROLE)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), RoleAddedToUserEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserRemovedRoleEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = RoleRemovedFromUserEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .role(DEFAULT_ROLE)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), RoleRemovedFromUserEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserModifyDiscordEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserDiscordModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserDiscordModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserModifyEmailEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserEmailModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserEmailModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserModifyNameEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserNameModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserNameModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserModifyNamespaceEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserNamespaceModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserNamespaceModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserModifyNamespaceAndNameEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserNamespaceAndNameModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserNamespaceAndNameModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReceiveUserSubjectModificationEventWhenItIsSent() throws IOException {
    log.entry();
    
    // Given
    final var event = UserSubjectModificationEvent.builder()
            .application("kp-users")
            .user(DEFAULT_USER)
            .build();
    
    bus.publishEvent(event);
    
    var result = output.receive(0L, "kp-users.modification");
    
    assertNotNull(result);
    assertEquals(event, jsonMapper.readValue(result.getPayload(), UserSubjectModificationEvent.class));
    
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
  
  private static final KpRole DEFAULT_ROLE = KpRole.builder()
      .nameSpace("namespace")
      .name("name")
      
      .created(OffsetDateTime.now())
      .modified(OffsetDateTime.now())
      
      .build();
}
