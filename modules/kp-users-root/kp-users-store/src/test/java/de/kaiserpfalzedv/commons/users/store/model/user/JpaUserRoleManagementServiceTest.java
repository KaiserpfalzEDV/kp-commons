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
import de.kaiserpfalzedv.commons.users.domain.model.role.KpRole;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleAddedToUserEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.RoleRemovedFromUserEvent;
import de.kaiserpfalzedv.commons.users.store.model.role.JpaRoleReadService;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaUserRoleManagementServiceTest {
  
  @InjectMocks
  private JpaUserRoleManagementService sut;
  
  @Mock
  private JpaRoleReadService jpaRoleReadService;
  
  @Mock
  private UserRepository repository;
  
  @Mock
  private EventBus bus;
  
  @Mock
  private RoleToJpaImpl toJpa;
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now();
  private static final UUID DEFAULT_ROLE_ID = UUID.randomUUID();

  private UserJPA jpaUser;
  private Role role;
  private RoleJPA jpaRole;
  
  
  @BeforeEach
  public void setUp() {
    reset(bus, repository, toJpa);
    
    jpaUser = UserJPA.builder()
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
    
    role = KpRole.builder()
        .id(DEFAULT_ROLE_ID)
        
        .nameSpace("namespace")
        .name("role")
        
        .created(CREATED_AT)
        .modified(CREATED_AT)
        
        .build();
    
    jpaRole = RoleJPA.builder()
        .nameSpace("namespace")
        .name("role")
        
        .version(0)
        
        .created(CREATED_AT)
        .modified(CREATED_AT)
        
        .build();
    
  }
  
  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bus, repository, toJpa);
    validateMockitoUsage();
  }
  
  
  @Test
  void shouldAddRoleToUserWhenUserExists() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.of(jpaRole));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(jpaUser.toBuilder().authorities(Set.of(jpaRole)).build());
    
    sut.addRole(DEFAULT_ID, role);
    
    verify(repository).saveAndFlush(jpaUser);
    verify(bus).post(any(RoleAddedToUserEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldDoNothingWhenUserHasRoleAlready() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    jpaUser.addRole(bus, jpaRole);
    reset(bus);
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.of(jpaRole));
    
    sut.addRole(DEFAULT_ID, role);
    
    verify(repository).saveAndFlush(jpaUser);
    verify(bus, never()).post(any(RoleAddedToUserEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowRoleNotFoundExceptionWhenRoleDoesNotExistForAddRole() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.empty());
    
    assertThrows(RoleNotFoundException.class, () -> sut.addRole(DEFAULT_ID, role));
    
    verify(repository).findById(DEFAULT_ID);
    verify(jpaRoleReadService).retrieve(DEFAULT_ROLE_ID);
    
    log.exit();
  }
  
  @Test
  void shouldThrowUserNotFoundExceptionwhenUserDoesNotExistForAddRole() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.addRole(DEFAULT_ID, role));
    
    verify(repository).findById(DEFAULT_ID);
    
    log.exit();
  }
  
  
  @Test
  void shouldRemoveRoleFromUserWhenUserWithRoleExists() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    jpaUser.addRole(bus, jpaRole);
    reset(bus);
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.of(jpaRole));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(jpaUser.toBuilder().authorities(Set.of()).build());
    
    sut.removeRole(DEFAULT_ID, role);
    
    verify(repository).saveAndFlush(jpaUser);
    verify(bus).post(any(RoleRemovedFromUserEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldDoNothingWhenUserWithoutRoleExists() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.of(jpaRole));
    
    sut.removeRole(DEFAULT_ID, role);
    
    verify(repository).saveAndFlush(jpaUser);
    verify(bus, never()).post(any(RoleRemovedFromUserEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowUserNotFoundExceptionWhenUserDoesNotExistForRemoveRole() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.removeRole(DEFAULT_ID, role));
    
    verify(repository).findById(DEFAULT_ID);
    
    log.exit();
  }
  
  @Test
  void shouldThrowRoleNotFoundExceptionWhenRoleDoesNotExistForRemoveRole() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(jpaUser));
    when(jpaRoleReadService.retrieve(DEFAULT_ROLE_ID)).thenReturn(Optional.empty());
    
    assertThrows(RoleNotFoundException.class, () -> sut.removeRole(DEFAULT_ID, role));
    
    verify(repository).findById(DEFAULT_ID);
    verify(jpaRoleReadService).retrieve(DEFAULT_ROLE_ID);
    
    log.exit();
  }
  
  @Test
  void shouldThrowUnsupportedWhenRemovingRoleFromAllUsers() {
    log.entry();
    
    assertThrows(UnsupportedOperationException.class, () -> sut.revokeRoleFromAllUsers(role));
    
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
  
  
}
