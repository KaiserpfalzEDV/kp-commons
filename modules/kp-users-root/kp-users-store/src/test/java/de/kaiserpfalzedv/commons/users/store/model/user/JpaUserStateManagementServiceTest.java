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
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserBannedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserDetainedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserReleasedEvent;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleToJpaImpl;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaUserStateManagementServiceTest {
  
  @InjectMocks
  private JpaUserStateManagementService sut;
  
  @Mock
  private UserRepository repository;
  
  @Mock
  private EventBus bus;
  
  @Mock
  private RoleToJpaImpl toJpa;
  
  
  @BeforeEach
  public void setUp() {
    reset(bus, repository, toJpa);
  }
  
  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bus, repository, toJpa);
    validateMockitoUsage();
  }

  
  @Test
  void shouldDetainUserWhenActive() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().build());
    
    sut.detain(DEFAULT_ID, 1);
    
    verify(bus).post(any(UserDetainedEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowUserNotFoundExceptionWhenDetainingANonExistingUser() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.detain(DEFAULT_ID, 1));
    
    verify(bus, never()).post(any(UserDetainedEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldBanUserWhenActive() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().build());
    
    sut.ban(DEFAULT_ID);
    
    verify(bus).post(any(UserBannedEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowUserNotFoundExceptionWhenBanningANonExistingUser() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.ban(DEFAULT_ID));
    
    verify(bus, never()).post(any(UserBannedEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldReleaseUserWhenBanned() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_JPA_USER.toBuilder().bannedOn(CREATED_AT).build()));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER);
    
    sut.release(DEFAULT_ID);
    
    verify(bus).post(any(UserReleasedEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowUserNotFoundExceptionWhenReleasingANonExistingUser() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.release(DEFAULT_ID));
    
    verify(bus, never()).post(any(UserReleasedEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldRegisterFromEventBus() {
    log.entry();
    
    sut.init();
    
    verify(bus).register(sut);
    
    log.exit();
  }
  
  @Test
  void shouldUnregisterFromEventBus() {
    log.entry();
    
    sut.close();
    
    verify(bus).unregister(sut);
    
    log.exit();
  }
  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now();
  private static final UserJPA DEFAULT_JPA_USER = UserJPA.builder()
      .id(DEFAULT_ID)
      
      .nameSpace("namespace")
      .name("name")
      
      .issuer("issuer")
      .subject(DEFAULT_ID.toString())
      
      .email("email@email.email")
      
      .version(0)
      .revId(0)
      .revisioned(CREATED_AT)
      
      .created(CREATED_AT)
      .modified(CREATED_AT)
      
      .build();
}
