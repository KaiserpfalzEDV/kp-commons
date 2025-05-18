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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@XSlf4j
public class SendUserActivityEventsConfigTest {
  private final SendUserActivityEventsConfig sut = new SendUserActivityEventsConfig("kp-users");
  
  @Test
  void shouldSendUserLoginEventWhenEventHasBeenGeneratedLocally() {
    log.entry();
    
    UserLoginEvent event = mock(UserLoginEvent.class);
    when(event.getApplication()).thenReturn("kp-users");
    
    // given
    
    // when
    var result = sut.sendUserLogin().apply(event);
    
    // then
    assertEquals(event, result);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUserEventWhenEventHasBeenGeneratedRemotely() {
    log.entry();
    
    // given
    var event = mock(UserLoginEvent.class);
    when(event.getApplication()).thenReturn("other-scs");
    
    // when
    var result = sut.sendUserLogin().apply(event);
    
    // then
    assertNull(result);
    
    log.exit();
  }
}
