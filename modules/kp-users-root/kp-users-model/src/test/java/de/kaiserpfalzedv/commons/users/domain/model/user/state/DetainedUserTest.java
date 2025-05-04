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


import com.google.common.eventbus.Subscribe;
import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.events.arbitation.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.events.state.*;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@XSlf4j
public class DetainedUserTest {
  private final LoggingEventBus bus = new LoggingEventBus();
  
  private UserState sut;
  
  
  @BeforeEach
  public void setUp() {
    sut = KpUserDetails.builder()
        .detainedTill(OffsetDateTime.now().plusDays(30L))
        .detainmentDuration(Duration.ofDays(90L))
        .build()
        .getState(bus);
    
    bus.register(this);
  }
  
  @AfterEach
  public void tearDown() {
    bus.unregister(this);
    
    activeUser = null;
    bannedUser = null;
    detainedUser = null;
    releasedUser = null;
    deletedUser = null;
    removedUser = null;
    petitionUser = null;
  }
  
  @Test
  public void shouldReturnDetainedUserWhenActivating() {
    log.entry();
    
    UserState result = sut.activate();
    assertInstanceOf(DetainedUser.class, result);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(deletedUser, "There should be no deleted user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit();
  }
  
  private UserReleasedEvent releasedUser = null;
  @Subscribe
  public void onReleasedUser(UserReleasedEvent event) {
    log.entry(event);
    this.releasedUser = event;
    log.exit(event);
  }

  
  @Test
  public void shouldReturnDetainedUserWhenDetaining() {
    log.entry();

    UserState result = sut.detain(45L);
    assertInstanceOf(DetainedUser.class, result);
    assertNotNull(detainedUser);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(deletedUser, "There should be no deleted user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }
  
  private UserDetainedEvent detainedUser = null;
  @Subscribe
  public void onDetainedUser(UserDetainedEvent detainedUser) {
    log.entry(detainedUser);
    this.detainedUser = detainedUser;
    log.exit(detainedUser);
  }
  
  
  @Test
  public void shouldReturnActiveUserWhenReleasingANonDeletedUser() {
    log.entry();
    
    UserState result = sut.release();
    assertInstanceOf(ActiveUser.class, result);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNotNull(releasedUser);
    assertNull(deletedUser, "There should be no deleted user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }
  
  @Test
  public void shouldReturnDeletedUserWhenReleasingADeletedUser() {
    log.entry();
    
    sut = KpUserDetails.builder()
        .deleted(OffsetDateTime.now().minusDays(1L))
        .detainedTill(OffsetDateTime.now().plusDays(2L))
        .detainmentDuration(Duration.ofDays(3L))
        .build()
        .getState(bus);
    
    UserState result = sut.release();
    assertInstanceOf(DeletedUser.class, result);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNotNull(releasedUser);
    assertNull(deletedUser, "There should be no deleted user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }
  
  
  private UserActivatedEvent activeUser = null;
  @Subscribe
  public void onActiveUser(UserActivatedEvent activeUser) {
    log.entry(activeUser);
    this.activeUser = activeUser;
    log.exit(activeUser);
  }
  
  
  @Test
  public void shouldReturnBannedUserWhenBanning() {
    log.entry();
    
    UserState result = sut.ban();
    assertInstanceOf(BannedUser.class, result);
    assertNull(activeUser, "There should be no activating user event.");
    assertNotNull(bannedUser);
    assertNull(detainedUser, "There should be no detained user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(deletedUser, "There should be no deleted user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }
  
  private UserBannedEvent bannedUser = null;
  @Subscribe
  public void onBannedUser(UserBannedEvent bannedUser) {
    log.entry(bannedUser);
    this.bannedUser = bannedUser;
    log.exit(bannedUser);
  }
  
  
  @Test
  public void shouldReturnDeletedUserWhenDeleting() {
    log.entry();
    
    UserState result = sut.delete();
    assertInstanceOf(DeletedUser.class, result);
    assertNotNull(deletedUser);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(removedUser, "There should be no removed user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }

  private UserDeletedEvent deletedUser = null;
  @Subscribe
  public void onDeletedUser(UserDeletedEvent deletedUser) {
    log.entry(deletedUser);
    this.deletedUser = deletedUser;
    log.exit(deletedUser);
  }
  
  
  @Test
  public void shouldReturnDeletedUserWhenRemoving() {
    log.entry();
    
    UserState result = sut.remove(true);
    
    assertInstanceOf(DeletedUser.class, result);
    assertNotNull(deletedUser);
    assertNull(removedUser, "There should be no removing user event.");
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(petitionUser, "There should be no petition user event.");
    
    log.exit(result);
  }
  
  private UserRemovedEvent removedUser = null;
  @Subscribe
  public void onRemovedUser(UserRemovedEvent event) {
    log.entry(event);
    removedUser = event;
    log.exit(event);
  }
  
  
  @Test
  public void shouldReturnDetainedUserWhenPetitioning() {
    log.entry();
    
    UserState result = sut.petition(UUID.randomUUID());
    
    assertInstanceOf(DetainedUser.class, result);
    assertNotNull(petitionUser);
    assertNull(activeUser, "There should be no activating user event.");
    assertNull(bannedUser, "There should be no banned user event.");
    assertNull(detainedUser, "There should be no detained user event.");
    assertNull(releasedUser, "There should be no released user event.");
    assertNull(deletedUser, "There should be no activating user event.");
    assertNull(removedUser, "There should be no banned user event.");
    
    log.exit(result);
  }
  
  private UserPetitionedEvent petitionUser = null;
  @Subscribe
  public void onPetitionedUser(UserPetitionedEvent event) {
    log.entry(event);
    petitionUser = event;
    log.exit(event);
  }
}
