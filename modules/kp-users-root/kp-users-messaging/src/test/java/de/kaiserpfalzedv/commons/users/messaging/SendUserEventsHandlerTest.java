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


import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserPetitionedEvent;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-24
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class SendUserEventsHandlerTest {
  @InjectMocks private SendUserArbitrationEventsHandler sut;
  
  @Mock StreamBridge streamBridge;
  @Mock UserEventMessagingConverter converter;

  
  @BeforeEach
  public void setUp() {
    reset(streamBridge, converter);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(streamBridge, converter);
  }
  
  
  @Test
  void shouldSendEventWhenMessageIsNotNull() {
    final UserPetitionedEvent event = Mockito.mock(UserPetitionedEvent.class);
    //noinspection rawtypes
    final Message message = Mockito.mock(Message.class);
    
    when(event.getApplication()).thenReturn("kp-users");
    when(converter.headers(eq(event))).thenReturn(new MessageHeaders(Collections.singletonMap("kp-user-id", UUID.randomUUID().toString())));
    //noinspection unchecked
    when(converter.toMessage(eq(event), any())).thenReturn(message);
    when(streamBridge.send(any(), any())).thenReturn(true);
    
    sut.onUserPetitioned(event);
  }
  
  @Test
  void shouldNotSendEventWhenMessageIsNull() {
    final UserPetitionedEvent event = Mockito.mock(UserPetitionedEvent.class);
    
    when(event.getApplication()).thenReturn("kp-users");
    when(converter.headers(eq(event))).thenReturn(new MessageHeaders(Collections.singletonMap("kp-user-id", UUID.randomUUID().toString())));
    when(converter.toMessage(eq(event), any())).thenReturn(null);
    
    assertThrows(IllegalArgumentException.class, () -> sut.onUserPetitioned(event));
  }
}
