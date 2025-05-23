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
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@SpringBootTest(
    classes = {SpringEventBus.class, TestEventListener.class}
)
@XSlf4j
public class RemovedUserTest {
  @Autowired private SpringEventBus bus;
  @Autowired private TestEventListener listener;
  
  private UserState sut;
  
  
  @BeforeEach
  public void setUp() {
    sut = KpUserDetails.builder()
        .deleted(OffsetDateTime.now().minusDays(1L))
        .build()
        .getState(bus).remove(false);
    listener.clear();
    
    bus.register(this);
  }
  
  @AfterEach
  public void tearDown() {
    bus.unregister(this);
    
    listener.clear();
  }

  
  @Test
  public void shouldReturnActiveUserWhenActivatingAnUnbannedAndUndetainedUser() {
    log.entry();
    
    UserState result = sut.activate();
    assertInstanceOf(ActiveUser.class, result);
    assertNotNull(listener.getActiveUser());
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit();
  }
  
  
  @Test
  public void shouldReturnBannedUserWhenActivatingABannedUser() {
    log.entry();
    
    sut = KpUserDetails.builder()
        .deleted(OffsetDateTime.now().minusDays(1L))
        .bannedOn(OffsetDateTime.now().minusDays(2L))
        .build()
        .getState(bus).remove(false);
    listener.clear();
    
    
    UserState result = sut.activate();
    assertInstanceOf(BannedUser.class, result);
    assertNotNull(listener.getActiveUser());
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit();
  }
  
  
  @Test
  public void shouldReturnDetainedUserWhenActivatingADetainedUser() {
    log.entry();
    
    sut = KpUserDetails.builder()
        .deleted(OffsetDateTime.now().minusDays(1L))
        .detainmentDuration(Duration.ofDays(30L))
        .detainedTill(OffsetDateTime.now().plusDays(1L))
        .build()
        .getState(bus).remove(false);
    listener.clear();
    
    UserState result = sut.activate();
    assertInstanceOf(DetainedUser.class, result);
    assertNotNull(listener.getActiveUser());
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit();
  }

  
  @Test
  public void shouldReturnRemovedUserWhenDetaining() {
    log.entry();

    UserState result = sut.detain(45L);
    assertInstanceOf(RemovedUser.class, result);
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnRemovedUserWhenReleasing() {
    log.entry();
    
    UserState result = sut.release();
    assertInstanceOf(RemovedUser.class, result);
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNotNull(listener.getReleasedUser());
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnRemovedUserWhenBanning() {
    log.entry();
    
    UserState result = sut.ban();
    assertInstanceOf(RemovedUser.class, result);
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNotNull(listener.getBannedUser());
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnRemovedUserWhenDeleting() {
    log.entry();
    
    UserState result = sut.delete();
    assertInstanceOf(RemovedUser.class, result);
    assertNull(listener.getDeletedUser(), "There should be no deleting user event.");
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnRemovedUserWhenRemoving() {
    log.entry();
    
    UserState result = sut.remove(true);
    
    assertInstanceOf(RemovedUser.class, result);
    assertNull(listener.getDeletedUser(), "There should be no deleting user event.");
    assertNull(listener.getRemovedUser(), "There should be no removing user event.");
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnDeletedUserWhenPetitioning() {
    log.entry();
    
    UserState result = sut.petition(UUID.randomUUID());
    
    assertInstanceOf(RemovedUser.class, result);
    assertNotNull(listener.getPetitionUser());
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no activating user event.");
    assertNull(listener.getRemovedUser(), "There should be no banned user event.");
    
    log.exit(result);
  }
}
