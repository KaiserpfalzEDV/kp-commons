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

package de.kaiserpfalzedv.commons.users.store.service;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKey;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKeyImpl;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.store.model.apikey.JpaApiKeyWriteService;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-17
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaApiKeyEventsHandlerTest {
  @InjectMocks
  private JpaApiKeyEventsHandler sut;
  
  @Mock
  private JpaApiKeyWriteService writeService;
  
  @Mock
  private EventBus bus;
  
  
  private static final String LOCAL_SYSTEM = "kp-commons";
  private static final String EXTERNAL_SYSTEM = "other-application";
  
  
  @BeforeEach
  public void setUp() {
    reset(writeService, bus);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(writeService, bus);
  }

  
  @Test
  void shouldHandleApiKeyCreatedEventWhenEventIsFromExternalSystem() throws InvalidApiKeyException {
    log.entry();
    
    // given
    ApiKeyCreatedEvent event = mock(ApiKeyCreatedEvent.class);
    when(event.getApiKey()).thenReturn(API_KEY);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).create(API_KEY);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionWhileCreatingApiKeyWhenEventIsFromExternalSystem() throws InvalidApiKeyException {
    log.entry();
    
    // given
    ApiKeyCreatedEvent event = mock(ApiKeyCreatedEvent.class);
    when(event.getApiKey()).thenReturn(API_KEY);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);

    doThrow(InvalidApiKeyException.class).when(writeService).create(API_KEY);

    // when
    sut.event(event);
    
    // then
    verify(writeService).create(API_KEY);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreApiKeyCreatedEventWhenEventIsFromLocalSystem() throws InvalidApiKeyException {
    log.entry();
    
    // given
    ApiKeyCreatedEvent event = mock(ApiKeyCreatedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService, never()).create(API_KEY);
    
    log.exit();
  }
  
  
  @Test
  void shouldHandleApiKeyRevokedEventWhenEventIsFromExternalSystem() {
    log.entry();
    
    // given
    ApiKeyRevokedEvent event = mock(ApiKeyRevokedEvent.class);
    when(event.getApiKey()).thenReturn(API_KEY);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).delete(DEFAULT_ID);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreApiKeyRevokedEventWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    ApiKeyRevokedEvent event = mock(ApiKeyRevokedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService, never()).delete(DEFAULT_ID);
    
    log.exit();
  }
  
  
  @Test
  void shouldRegisterToEventBus() {
    log.entry();
    
    // when
    sut.init();
    
    // then
    verify(bus).register(sut);
    
    log.exit();
  }
  
  @Test
  void shouldUnregisterFromEventBus() {
    log.entry();
    
    // when
    sut.close();
    
    // then
    verify(bus).unregister(sut);
    
    log.exit();
  }

  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime DEFAULT_CREATED = OffsetDateTime.now().minusDays(100);
  private static final OffsetDateTime DEFAULT_MODIFIED = DEFAULT_CREATED;
  private static final OffsetDateTime DEFAULT_EXPIRES = DEFAULT_CREATED.plusDays(180);

  private static final User DEFAULT_USER = KpUserDetails.builder()
      .id(UUID.randomUUID())
      .created(DEFAULT_CREATED)
      .modified(DEFAULT_MODIFIED)
      
      .issuer("issuer")
      .subject("subject")
      
      .nameSpace("namespace")
      .name("name")
      
      .email("email@email.email")
      .discord("discord")
      .build();
  private static final ApiKey API_KEY = ApiKeyImpl.builder()
      .id(DEFAULT_ID)
      .created(DEFAULT_CREATED)
      .modified(DEFAULT_MODIFIED)
      
      .user((KpUserDetails) DEFAULT_USER)
      .expiration(DEFAULT_EXPIRES)
      .build();
}
