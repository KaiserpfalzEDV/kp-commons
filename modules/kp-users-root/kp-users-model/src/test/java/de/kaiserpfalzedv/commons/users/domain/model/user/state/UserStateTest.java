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

package de.kaiserpfalzedv.commons.users.domain.model.user.state;


import de.kaiserpfalzedv.commons.spring.events.SpringEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.TestEventListener;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@SpringBootTest(
    classes = {SpringEventBus.class, TestEventListener.class}
)
@XSlf4j
public class UserStateTest {
  @Autowired private SpringEventBus bus;

  
  @Test
  public void shouldReturnActiveUserForAnActiveUser() {
    log.entry();
    
    User input = KpUserDetails.builder()
            .build();
    
    UserState result = UserState.Factory.fromUser(input, bus);
    
    assertInstanceOf(ActiveUser.class, result);
    assertFalse(result.isInactive());
    assertTrue(result.isActive());
    assertFalse(result.isBanned());
    assertFalse(result.isDetained());
    assertFalse(result.isDeleted());
    
    log.exit(result);
  }

  
  @Test
  public void shouldReturnBannedUserForABannedUser() {
    log.entry();
    
    User input = KpUserDetails.builder()
        .bannedOn(OffsetDateTime.now().minusDays(1L))
        .build();
    
    UserState result = UserState.Factory.fromUser(input, bus);
    
    assertInstanceOf(BannedUser.class, result);
    assertTrue(result.isInactive());
    assertFalse(result.isActive());
    assertTrue(result.isBanned());
    assertFalse(result.isDetained());
    assertFalse(result.isDeleted());
    
    log.exit(result);
  }
  
  @Test
  public void shouldReturnDetainedUserForADetainedUser() {
    log.entry();
    
    User input = KpUserDetails.builder()
        .detainmentDuration(Duration.ofDays(30L))
        .detainedTill(OffsetDateTime.now().plusDays(21L))
        .build();
    
    UserState result = UserState.Factory.fromUser(input, bus);
    
    assertInstanceOf(DetainedUser.class, result);
    assertTrue(result.isInactive());
    assertFalse(result.isActive());
    assertFalse(result.isBanned());
    assertTrue(result.isDetained());
    assertFalse(result.isDeleted());
    
    log.exit(result);
  }
  
  @Test
  public void shouldReturnDeletedUserForADeletedUser() {
    log.entry();
    
    User input = KpUserDetails.builder()
        .deleted(OffsetDateTime.now().minusDays(93L))
        .build();
    
    UserState result = UserState.Factory.fromUser(input, bus);
    
    assertInstanceOf(DeletedUser.class, result);
    assertTrue(result.isInactive());
    assertFalse(result.isActive());
    assertFalse(result.isBanned());
    assertFalse(result.isDetained());
    assertTrue(result.isDeleted());
    
    log.exit(result);
  }
  
  @Test
  public void shouldReturnRemovedUserWhenRemovingAnUser() {
    log.entry();
    
    User input = KpUserDetails.builder()
        .build();
    
    UserState active = UserState.Factory.fromUser(input, bus);
    UserState result = active.remove(true);
    
    assertInstanceOf(RemovedUser.class, result);
    assertTrue(result.isInactive());
    assertFalse(result.isActive());
    assertFalse(result.isBanned());
    assertFalse(result.isDetained());
    assertTrue(result.isDeleted());
    
    log.exit(result);
  }
}
