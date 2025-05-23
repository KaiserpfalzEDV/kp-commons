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

package de.kaiserpfalzedv.commons.users.store.model.user;

import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleAddedToUserEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleRemovedFromUserEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@XSlf4j
class UserJPATest {
  private UserJPA user;
  
  @Mock
  private EventBus bus;
  
  @BeforeEach
  void setUp() {
    reset(bus);
    
    user = UserJPA.builder()
        .issuer("issuer")
        .subject("subject")
        .nameSpace("namespace")
        .name("username")

        .version(0)
        .created(DEFAULT_CREATE_TIME)
        .modified(DEFAULT_CREATE_TIME)
        .build();
  }
  
  @AfterEach
  void tearDown() {
    validateMockitoUsage();
  }

  
  @Test
  void shouldSendEventsAndDetainTheUserWhenUserIsValid() {
    log.entry();
    
    user.detain(bus, 5);

    verify(bus).post(any(UserDetainedEvent.class));
    assertNotNull(user.getDetainedTill());
    assertNotNull(user.getDetainmentDuration());
    assertEquals(5, user.getDetainmentDuration().toDays());
    
    log.exit();
  }
  
  @Test
  void shouldSendEventsAndReleaseUserWhenUserIsDetained() {
    log.entry();
    
    user.detain(bus, 5);
    reset(bus);
    
    user.release(bus);
    
    verify(bus).post(any(UserReleasedEvent.class));
    assertNull(user.getDetainedTill());
    assertNull(user.getDetainmentDuration());
    
    log.exit();
  }
  
  @Test
  void shouldSendEventsAndReleaseUserWhenUserIsBanned() {
    log.entry();
    
    user.ban(bus);
    reset(bus);
    
    user.release(bus);
    
    verify(bus).post(any(UserReleasedEvent.class));
    assertNull(user.getBannedOn());
    
    log.exit();
  }
  
  @Test
  void shouldBanUserWhenUserIsActive() {
    log.entry();
    
    user.ban(bus);
    
    verify(bus).post(any(UserBannedEvent.class));
    assertNotNull(user.getBannedOn());
    
    log.exit();
  }
  
  @Test
  void shouldMarkUserAsDeletedWhenUserIsActive() {
    log.entry();
    
    user.delete(bus);
    
    verify(bus).post(any(UserDeletedEvent.class));
    assertNotNull(user.getDeleted());
    
    log.exit();
  }
  
  @Test
  void shouldUnmarkUserWhenUserIsDeleted() {
    log.entry();
    
    user.undelete(bus);
    
    verify(bus).post(any(UserActivatedEvent.class));
    assertNull(user.getDeleted());
    
    log.exit();
  }
  
  @Test
  void shouldAddRoleWhenUserDoesNotHaveTheRole() {
    log.entry();
    
    user.addRole(bus, DEFAULT_ROLE);

    verify(bus).post(any(RoleAddedToUserEvent.class));
    assertTrue(user.getAuthorities().contains(DEFAULT_ROLE));
    
    log.exit();
  }
  
  @Test
  void shouldNotAddRoleWhenUserDoesHaveTheRoleAlready() {
    log.entry();
    
    user.addRole(bus, DEFAULT_ROLE);
    reset(bus);
    
    user.addRole(bus, DEFAULT_ROLE);
    
    verify(bus, never()).post(any(RoleAddedToUserEvent.class));
    assertTrue(user.getAuthorities().contains(DEFAULT_ROLE));
    
    log.exit();
  }
  
  @Test
  void shouldRemoveTheRoleWhenUserDoesHaveTheRole() {
    log.entry();
    
    user.addRole(bus, DEFAULT_ROLE);
    reset(bus);
    
    user.removeRole(bus, DEFAULT_ROLE);
    verify(bus).post(any(RoleRemovedFromUserEvent.class));
    
    assertFalse(user.getAuthorities().contains(DEFAULT_ROLE));
    
    log.exit();
  }
  
  @Test
  void shouldNotRemoveTheRoleWhenUserDoesNotHaveTheRole() {
    log.entry();
    
    user.removeRole(bus, DEFAULT_ROLE);
    verify(bus, never()).post(any(RoleRemovedFromUserEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldDoNothingWhenUserCredentialsGetErased() {
    log.entry();
    
    user.eraseCredentials();
    
    // nothing to check because nothing should have been done ...
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowAnExceptionWhenInvalidEmailIsSet() {
    log.entry();
    
    user.toBuilder().email("invalid-email").build();
  }
  

  private static final OffsetDateTime DEFAULT_CREATE_TIME = OffsetDateTime.now();
  private static final RoleJPA DEFAULT_ROLE = RoleJPA.builder()
      .nameSpace("namespace")
      .name("name")
      .version(0)
      .created(DEFAULT_CREATE_TIME)
      .modified(DEFAULT_CREATE_TIME)
      .build();
}
