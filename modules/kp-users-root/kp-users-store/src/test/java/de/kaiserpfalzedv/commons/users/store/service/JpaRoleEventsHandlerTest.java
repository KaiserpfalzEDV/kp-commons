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

package de.kaiserpfalzedv.commons.users.store.service;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.KpRole;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleRemovedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleUpdateNameEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleUpdateNameSpaceEvent;
import de.kaiserpfalzedv.commons.users.store.model.role.JpaRoleWriteService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserRoleManagementService;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-17
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaRoleEventsHandlerTest {
  @InjectMocks
  private JpaRoleEventsHandler sut;

  @Mock
  private JpaRoleWriteService writeService;

  @Mock
  private JpaUserRoleManagementService userRoleManagement;

  @Mock
  private EventBus bus;
  
  private static final String LOCAL_SYSTEM = "kp-commons";
  private static final String EXTERNAL_SYSTEM = "other-application";
  
  
  private static final UUID TEST_ROLE_ID = UUID.randomUUID();
  private static final String TEST_ROLE_NAME = "Test Role";
  private static final String TEST_ROLE_NAMESPACE = "Test Namespace";
  private static final OffsetDateTime TEST_ROLE_CREATED_TIME = OffsetDateTime.now().minusDays(100);
  private Role role;

  @BeforeEach
  void setUp() {
      reset(writeService, userRoleManagement, bus);
      
      role = KpRole.builder()
          .id(TEST_ROLE_ID)
          
          .name(TEST_ROLE_NAME)
          .nameSpace(TEST_ROLE_NAMESPACE)
          
          .created(TEST_ROLE_CREATED_TIME)
          .modified(TEST_ROLE_CREATED_TIME)
          
          .build();
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(writeService, userRoleManagement, bus);
  }
  
  
  @Test
  void shouldCreateRoleOnRoleCreatedEventWhenEventIsFromExternalSystem() throws RoleCantBeCreatedException {
    log.entry();
    
    // given
    RoleCreatedEvent event = mock(RoleCreatedEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).create(role);
  }
  
  @Test
  void shouldHandleExceptionWhenCreationFailsWhenEventIsFromExternalSystem() throws RoleCantBeCreatedException {
    log.entry();
    
    // given
    RoleCreatedEvent event = mock(RoleCreatedEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    doThrow(new RoleCantBeCreatedException(role, new IllegalStateException())).when(writeService).create(role);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).create(role);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreRoleOnRoleCreatedEventWhenEventIsFromLocalSystem() throws RoleCantBeCreatedException {
    log.entry();
    
    // given
    RoleCreatedEvent event = mock(RoleCreatedEvent.class);
    when(event.getSystem()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService, never()).create(role);
  }
  
  
  @Test
  void shouldChangeNameSpaceOnRoleUpdateNameSpaceEventWhenEventIsFromExternalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameSpaceEvent event = mock(RoleUpdateNameSpaceEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).updateNameSpace(role.getId(), role.getNameSpace());
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionOnRoleUpdateNameSpaceEventWhenEventIsFromExternalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameSpaceEvent event = mock(RoleUpdateNameSpaceEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);

    
    doThrow(new RoleNotFoundException(TEST_ROLE_ID)).when(writeService).updateNameSpace(TEST_ROLE_ID, TEST_ROLE_NAMESPACE);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).updateNameSpace(TEST_ROLE_ID, TEST_ROLE_NAMESPACE);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreNameSpaceOnRoleUpdateNameSpaceEventWhenEventIsFromLocalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameSpaceEvent event = mock(RoleUpdateNameSpaceEvent.class);
    when(event.getSystem()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService, never()).updateNameSpace(role.getId(), role.getNameSpace());
    
    log.exit();
  }
  
  
  @Test
  void shouldChangeNameOnRoleUpdateNameEventWhenEventIsFromExternalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameEvent event = mock(RoleUpdateNameEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).updateName(role.getId(), role.getName());
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionOnRoleUpdateNameEventWhenEventIsFromExternalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameEvent event = mock(RoleUpdateNameEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    doThrow(new RoleNotFoundException(TEST_ROLE_ID)).when(writeService).updateName(TEST_ROLE_ID, TEST_ROLE_NAME);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService).updateName(TEST_ROLE_ID, TEST_ROLE_NAME);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreNameOnRoleUpdateNameSpaceEventWhenEventIsFromLocalSystem() throws RoleNotFoundException {
    log.entry();
    
    // given
    RoleUpdateNameEvent event = mock(RoleUpdateNameEvent.class);
    when(event.getSystem()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(writeService, never()).updateName(role.getId(), role.getName());
    
    log.exit();
  }
  
  
  @Test
  void shouldRemoveRoleOnRemoveEventWhenEventIsFromExternalSystem() {
    log.entry();
    
    // given
    RoleRemovedEvent event = mock(RoleRemovedEvent.class);
    when(event.getSystem()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).revokeRoleFromAllUsers(role);
    verify(writeService).remove(TEST_ROLE_ID);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreRoleOnRemoveEventWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    RoleRemovedEvent event = mock(RoleRemovedEvent.class);
    when(event.getSystem()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement, never()).revokeRoleFromAllUsers(role);
    verify(writeService, never()).remove(TEST_ROLE_ID);
    
    log.exit();
  }
  
  
  @Test
  void shouldRegisterToEventBus() {
    log.entry();
    
    // when
    sut.init();
    
    // then
    verify(bus).register(sut);
    
    log.exit();
  }
  
  @Test
  void shouldUnregisterFromEventBus() {
    log.entry();
    
    // when
    sut.close();
    
    // then
    verify(bus).unregister(sut);
    
    log.exit();
  }
}
