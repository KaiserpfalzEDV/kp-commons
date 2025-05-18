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
import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserActivityBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserArbitrationBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.UserDiscordModificationEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserBannedEvent;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class SendingOutgoingMessagesHandlerTest {
  @InjectMocks private SendingOutgoingMessagesHandler sut;
  @Mock private StreamBridge bridge;
  @Mock private LoggingEventBus bus;
  
  @BeforeEach
  public void setUp() {
    reset(bridge, bus);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(bridge, bus);
  }
  
  
  @Test
  void shouldSendUserEventWhenEventHasBeenGeneratedLocally() {
    log.entry();
    
    // given
    var event = mock(UserBaseEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge).send(eq("kp-commons.activity"), any(UserBaseEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUserEventWhenEventHasBeenGeneratedRemotely() {
    log.entry();
    
    // given
    var event = mock(UserBaseEvent.class);
    when(event.getApplication()).thenReturn("other-scs");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge, never()).send(anyString(), any(UserBaseEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUseActivityTopicWhenEventIsAnActivityEvents() {
    log.entry();
    
    // given
    var event = mock(UserLoginEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge).send(eq("kp-commons.activity"), any(UserActivityBaseEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUseArbitrationTopicWhenEventIsAnArbitrationEvent() {
    log.entry();
    
    // given
    var event = mock(UserPetitionedEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge).send(eq("kp-commons.arbitration"), any(UserArbitrationBaseEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUseModificationTopicWhenEventIsAModificationEvent() {
    log.entry();
    
    // given
    var event = mock(UserDiscordModificationEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge).send(eq("kp-commons.modification"), any(UserDiscordModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUseStateTopicWhenEventIsAStateEvent() {
    log.entry();
    
    // given
    var event = mock(UserBannedEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // when
    sut.event(event);
    
    // then
    verify(bridge).send(eq("kp-commons.state"), any(UserBannedEvent.class));
    
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
}
