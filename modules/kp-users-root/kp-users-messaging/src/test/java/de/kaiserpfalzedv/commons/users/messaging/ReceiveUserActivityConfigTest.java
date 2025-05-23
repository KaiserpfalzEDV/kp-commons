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


import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class ReceiveUserActivityConfigTest {
  @InjectMocks private ReceiveUserActivityConfig sut;
  @Mock private ApplicationEventPublisher bus;
  
  
  @BeforeEach
  public void setUp() {
    reset(bus);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(bus);
  }
  
  
  @Test
  void shouldIgnoreUserLoginEventWhenEventHasBeenGeneratedLocally() {
    log.entry();
    
    UserLoginEvent event = mock(UserLoginEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // given
    
    // when
    sut.loginUser().accept(event);
    
    // then
    verify(bus, never()).publishEvent(event);
    
    log.exit();
  }
  
  @Test
  void shouldPublishUserEventWhenEventHasBeenGeneratedRemotely() {
    log.entry();
    
    // given
    var event = mock(UserLoginEvent.class);
    when(event.getApplication()).thenReturn("other-scs");
    
    // when
    sut.loginUser().accept(event);
    
    // then
    verify(bus, times(1)).publishEvent(event);
    
    log.exit();
  }
}
