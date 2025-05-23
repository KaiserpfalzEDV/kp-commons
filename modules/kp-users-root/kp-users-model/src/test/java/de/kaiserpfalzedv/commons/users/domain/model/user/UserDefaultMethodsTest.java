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

package de.kaiserpfalzedv.commons.users.domain.model.user;

import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.users.domain.model.user.state.UserState;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@XSlf4j
@ExtendWith(MockitoExtension.class)
class UserDefaultMethodsTest {
  
  private User sut;
  
  @Mock
  private EventBus bus;
  
  @BeforeEach
  void setUp() {
    sut = TestUser.builder().build();
    
    reset(bus);
  }
  
  
  @Test
  void shouldUseStringUserAsABACObjectName() {
    log.entry("shouldUseStringUserAsABACObjectName");
    
    String result = sut.getABACObjectName();
    log.debug("ABACObjectName. result='{}'", result);
    
    assertEquals("User", result);
    
    log.exit(result);
  }
  
  @Test
  void shouldBeActiveWithDefaultUser() {
    log.entry("shouldBeActiveWithDefaultUser");
    
    boolean result = sut.isActive();
    log.debug("Checking activity. result={}", result);
    
    assertTrue(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotBeBannedWithDefaultUser() {
    log.entry("shouldNotBeBannedWithDefaultUser");
    
    boolean result = sut.isBanned();
    log.debug("Checking being banned. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotBeDetainedWithDefaultUser() {
    log.entry("shouldNotBeDetainedWithDefaultUser");
    
    boolean result = sut.isDetained();
    log.debug("Checking detained. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotBeDeletedWithDefaultUser() {
    log.entry("shouldNotBeDeletedWithDefaultUser");
    
    boolean result = sut.isDeleted();
    log.debug("Checking deleted. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotBeInactiveWithDefaultUser() {
    log.entry("shouldNotBeInactiveWithDefaultUser");
    
    boolean result = sut.isInactive();
    log.debug("Checking inactive. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
    
  }
  
  @Test
  void shouldBeTheUserIdWhenAskedForOwner() {
    log.entry("shouldBeTheUserIdWhenAskedForOwner");
    
    UUID result = sut.getOwnerId();
    log.debug("Checking owner id. result={}", result);
    
    assertEquals(sut.getId(), result);
    
    log.exit(result);
  }
  
  @Test
  void shouldBeTheUserWhenAskedForOwner() {
    log.entry("shouldBeTheUserWhenAskedForOwner");
    
    HasId<UUID> result = sut.getOwner();
    log.debug("Checking owner. result={}", result);
    
    assertEquals(sut, result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotThrowInactivityExceptionWhenAskedWithDefaultUser() throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException {
    log.entry("shouldNotBeInactiveWhenAskedWithDefaultUser");
    
    sut.checkInactive();
    
    log.exit();
  }
  
  @Test
  void shouldNotThrowBannedExceptionWhenAskedWithDefaultUser() throws UserIsBannedException {
    log.entry("shouldNotBeBannedWhenAskedWithDefaultUser");
    
    sut.checkBanned();
    
    log.exit();
  }
  
  @Test
  void shouldNotThrowDetainedExceptionWhenAskedWithDefaultUser() throws UserIsDetainedException {
    log.entry("shouldNotBeDetainedWhenAskedWithDefaultUser");
    
    sut.checkDetained();
    
    log.exit();
  }
  
  @Test
  void shouldNotThrowDeletedExceptionWhenAskedWithDefaultUser() throws UserIsDeletedException {
    log.entry("shouldNotBeDeletedWhenAskedWithDefaultUser");
    
    sut.checkDeleted();
    
    log.exit();
  }
  
  @Test
  void shouldNotHaveRoleABCWhenAskedWithRole() {
    log.entry("shouldNotHaveRoleABCWhenAskedWithRole");
    
    SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_ABC");
    
    boolean result = sut.hasRole(role);
    log.debug("Checking role 'ROLE_ABC' (SimpleGrantedAuthority). result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldNotHaveRoleABCWhenASkedWithString() {
    log.entry("shouldNotHaveRoleABCWhenASkedWithString");
    
    boolean result = sut.hasRole("ROLE_ABC");
    log.debug("Checking role 'ROLE_ABC' (String). result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldReturnAValidUserStateWhenAskedWithDefaultUser() {
    log.entry("shouldReturnAValidUserStateWhenAskedWithDefaultUser");
    
    UserState result = sut.getState(bus);
    log.debug("Checking user state. result={}", result);
    
    assertNotNull(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldThrowUnsupportedExceptionWhenAskedForPassword() {
    log.entry("shouldThrowUnsupportedExceptionWhenAskedForPassword");
    
    assertThrows(UnsupportedOperationException.class, () -> sut.getPassword());
    
    log.exit();
  }
  
  @Test
  void shouldReturnNameWhenAskedForUsername() {
    log.entry("shouldReturnNameWhenAskedForUsername");
    
    String result = sut.getUsername();
    log.debug("Checking username. result='{}'", result);
    
    assertEquals(sut.getName(), result);
  
    log.exit(result);
  }
}