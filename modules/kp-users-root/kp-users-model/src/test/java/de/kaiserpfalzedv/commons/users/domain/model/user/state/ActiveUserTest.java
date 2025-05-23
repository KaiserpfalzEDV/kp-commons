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
import de.kaiserpfalzedv.commons.users.domain.model.user.TestEventListener;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
public class ActiveUserTest {
  @Autowired private SpringEventBus bus;
  @Autowired private TestEventListener listener;
  
  
  private UserState sut;
  
  
  @BeforeEach
  public void setUp() {
    sut = KpUserDetails.builder().build().getState(bus);
    
    bus.register(this);
  }
  
  @AfterEach
  public void tearDown() {
    bus.unregister(this);

    listener.clear();
  }
  
  @Test
  public void shouldReturnActiveUserWhenActivating() {
    log.entry();
    
    UserState result = sut.activate();
    assertInstanceOf(ActiveUser.class, result);
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit();
  }
  
  
  @Test
  public void shouldReturnDetainedUserWhenDetaining() {
    log.entry();

    UserState result = sut.detain(45L);
    assertInstanceOf(DetainedUser.class, result);
    assertNotNull(listener.getDetainedUser());
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnActiveUserWhenReleasing() {
    log.entry();
    
    UserState result = sut.release();
    assertInstanceOf(ActiveUser.class, result);
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
  public void shouldReturnBannedUserWhenBanning() {
    log.entry();
    
    UserState result = sut.ban();
    assertInstanceOf(BannedUser.class, result);
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNotNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getDeletedUser(), "There should be no deleted user event.");
    assertNull(listener.getRemovedUser(), "There should be no removed user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnDeletedUserWhenDeleting() {
    log.entry();
    
    UserState result = sut.delete();
    assertInstanceOf(DeletedUser.class, result);
    assertNotNull(listener.getDeletedUser());
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
    assertNotNull(listener.getDeletedUser());
    assertNotNull(listener.getRemovedUser());
    assertNull(listener.getActiveUser(), "There should be no activating user event.");
    assertNull(listener.getBannedUser(), "There should be no banned user event.");
    assertNull(listener.getDetainedUser(), "There should be no detained user event.");
    assertNull(listener.getReleasedUser(), "There should be no released user event.");
    assertNull(listener.getPetitionUser(), "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldReturnActiveUserWhenPetitioning() {
    log.entry();
    
    UserState result = sut.petition(UUID.randomUUID());
    
    assertInstanceOf(ActiveUser.class, result);
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
