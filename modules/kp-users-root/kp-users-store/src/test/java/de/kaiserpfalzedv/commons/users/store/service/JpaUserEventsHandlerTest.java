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
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserDataManagementService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserManagementService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserRoleManagementService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserStateManagementService;
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
public class JpaUserEventsHandlerTest {
  @InjectMocks private JpaUserEventsHandler sut;
  @Mock private JpaUserManagementService userManagement;
  @Mock private JpaUserDataManagementService userDataManagement;
  @Mock private JpaUserStateManagementService userStateManagement;
  @Mock private JpaUserRoleManagementService userRoleManagement;
  @Mock private EventBus bus;
  
  
  private static final String LOCAL_SYSTEM = "kp-commons";
  private static final String EXTERNAL_SYSTEM = "other-application";
  
  private static final UUID USER_ID = UUID.randomUUID();
  private static final String NAMESPACE = "namespace";
  private static final String NAME  = "name";
  private static final String ISSUER = "issuer";
  private static final String SUBJECT = "subject";
  private static final String EMAIL = "email@email.email";
  private static final String DISCORD = "discord";
  private static final OffsetDateTime NOW = OffsetDateTime.now();
  
  private static final long DETAINEMENT_DAYS = 30L;
  
  private static final UUID TEST_ROLE_ID = UUID.randomUUID();
  private static final String TEST_ROLE_NAME = "Test Role";
  private static final String TEST_ROLE_NAMESPACE = "Test Namespace";
  private static final OffsetDateTime TEST_ROLE_CREATED_TIME = NOW.minusDays(100);
  
  
  private User user;
  private Role role;

  @BeforeEach
  void setUp() {
    reset(userManagement, userDataManagement, userStateManagement, userRoleManagement, bus);
    
    user = KpUserDetails.builder()
          .id(USER_ID)
          
          .issuer(ISSUER)
          .subject(SUBJECT)
          
          .name(NAME)
          .nameSpace(NAMESPACE)
          
          .email(EMAIL)
          .discord(DISCORD)
          
          .created(NOW)
          .modified(NOW)
          
          .build();
      
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
    verifyNoMoreInteractions(userManagement, userDataManagement, userStateManagement, userRoleManagement, bus);
  }
  
  
  @Test
  void shouldHandleActivationEventWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserActivatedEvent event = mock(UserActivatedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).undelete(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionOnActivationEventWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserActivatedEvent event = mock(UserActivatedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userManagement).undelete(USER_ID);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).undelete(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreActivationEventWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserActivatedEvent event = mock(UserActivatedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldCreateUserWhenEventIsFromExternalSystem() throws UserCantBeCreatedException {
    log.entry();
    
    // given
    UserCreatedEvent event = mock(UserCreatedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).create(user);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionOnCreateUserWhenEventIsFromExternalSystem() throws UserCantBeCreatedException {
    log.entry();
    
    // given
    UserCreatedEvent event = mock(UserCreatedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserCantBeCreatedException(ISSUER, SUBJECT, NAME, EMAIL)).when(userManagement).create(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).create(user);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreCreateUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserCreatedEvent event = mock(UserCreatedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldDeleteUserWhenEventIsFromExternalSystem() {
    log.entry();
    
    // given
    UserDeletedEvent event = mock(UserDeletedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).delete(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }

  @Test
  void shouldIgnoreDeleteUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserDeletedEvent event = mock(UserDeletedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldRemoveUserWhenEventIsFromExternalSystem() {
    log.entry();
    
    // given
    UserRemovedEvent event = mock(UserRemovedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userManagement).remove(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreRemoveUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserRemovedEvent event = mock(UserRemovedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldBanUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserBannedEvent event = mock(UserBannedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).ban(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionOnBanUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserBannedEvent event = mock(UserBannedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userStateManagement).ban(USER_ID);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).ban(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreBanUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserBannedEvent event = mock(UserBannedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldDetainUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserDetainedEvent event = mock(UserDetainedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getDays()).thenReturn(DETAINEMENT_DAYS);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).detain(USER_ID, DETAINEMENT_DAYS);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionDetainingUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserDetainedEvent event = mock(UserDetainedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getDays()).thenReturn(DETAINEMENT_DAYS);
    doThrow(new UserNotFoundException(USER_ID)).when(userStateManagement).detain(USER_ID, DETAINEMENT_DAYS);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).detain(USER_ID, DETAINEMENT_DAYS);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreDetainUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserDetainedEvent event = mock(UserDetainedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldPetitionUserWhenEventIsFromExternalSystem() {
    log.entry();
    
    // given
    UserPetitionedEvent event = mock(UserPetitionedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnorePetitionUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserPetitionedEvent event = mock(UserPetitionedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldReleaseUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserReleasedEvent event = mock(UserReleasedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).release(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleExceptionReleasingUserWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserReleasedEvent event = mock(UserReleasedEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userStateManagement).release(USER_ID);
    
    // when
    sut.event(event);
    
    // then
    verify(userStateManagement).release(USER_ID);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreReleasingUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserReleasedEvent event = mock(UserReleasedEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldAddRoleToUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleAddedToUserEvent event = mock(RoleAddedToUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).addRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundExceptionAddingRoleToUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleAddedToUserEvent event = mock(RoleAddedToUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    doThrow(new UserNotFoundException(USER_ID)).when(userRoleManagement).addRole(USER_ID, role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).addRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleRoleNotFoundExceptionAddingRoleToUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleAddedToUserEvent event = mock(RoleAddedToUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    doThrow(new RoleNotFoundException(TEST_ROLE_ID)).when(userRoleManagement).addRole(USER_ID, role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).addRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreAddingRoleToUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    RoleAddedToUserEvent event = mock(RoleAddedToUserEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldRemoveRoleFromUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleRemovedFromUserEvent event = mock(RoleRemovedFromUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).removeRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundExceptionRemovingRoleFromUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleRemovedFromUserEvent event = mock(RoleRemovedFromUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    doThrow(new UserNotFoundException(USER_ID)).when(userRoleManagement).removeRole(USER_ID, role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).removeRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleRoleNotFoundExeptionRemovingRoleFromUserWhenEventIsFromExternalSystem() throws UserNotFoundException, RoleNotFoundException {
    log.entry();
    
    // given
    RoleRemovedFromUserEvent event = mock(RoleRemovedFromUserEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    when(event.getRole()).thenReturn(role);
    doThrow(new RoleNotFoundException(TEST_ROLE_ID)).when(userRoleManagement).removeRole(USER_ID, role);
    
    // when
    sut.event(event);
    
    // then
    verify(userRoleManagement).removeRole(USER_ID, role);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreRemovingRoleFromUserWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    RoleRemovedFromUserEvent event = mock(RoleRemovedFromUserEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateSubjectWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserSubjectModificationEvent event = mock(UserSubjectModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateSubject(USER_ID, ISSUER, SUBJECT);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundExceptionUpdatingSubjectWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserSubjectModificationEvent event = mock(UserSubjectModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateSubject(USER_ID, ISSUER, SUBJECT);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateSubject(USER_ID, ISSUER, SUBJECT);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingSubjectWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserSubjectModificationEvent event = mock(UserSubjectModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldChangeNamespaceAndNameWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNamespaceAndNameModificationEvent event = mock(UserNamespaceAndNameModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateNamespaceAndName(USER_ID, NAMESPACE, NAME);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundUpdatingNamespaceAndNameWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNamespaceAndNameModificationEvent event = mock(UserNamespaceAndNameModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateNamespaceAndName(USER_ID, NAMESPACE, NAME);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateNamespaceAndName(USER_ID, NAMESPACE, NAME);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingNamespaceAndNameWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserNamespaceAndNameModificationEvent event = mock(UserNamespaceAndNameModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateNamespaceWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNamespaceModificationEvent event = mock(UserNamespaceModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateNamespace(USER_ID, NAMESPACE);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundUpdatingNamespaceWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNamespaceModificationEvent event = mock(UserNamespaceModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateNamespace(USER_ID, NAMESPACE);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateNamespace(USER_ID, NAMESPACE);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingNamespaceWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserNamespaceModificationEvent event = mock(UserNamespaceModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateNameWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNameModificationEvent event = mock(UserNameModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateName(USER_ID, NAME);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundUpdatingNameWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserNameModificationEvent event = mock(UserNameModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateName(USER_ID, NAME);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateName(USER_ID, NAME);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingNameWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserNameModificationEvent event = mock(UserNameModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateEmailWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserEmailModificationEvent event = mock(UserEmailModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateEmail(USER_ID, EMAIL);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundUpdatingEmailWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserEmailModificationEvent event = mock(UserEmailModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateEmail(USER_ID, EMAIL);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateEmail(USER_ID, EMAIL);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingEmailWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserEmailModificationEvent event = mock(UserEmailModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateDiscordWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserDiscordModificationEvent event = mock(UserDiscordModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateDiscord(USER_ID, DISCORD);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldHandleUserNotFoundUpdatingDiscordWhenEventIsFromExternalSystem() throws UserNotFoundException {
    log.entry();
    
    // given
    UserDiscordModificationEvent event = mock(UserDiscordModificationEvent.class);
    when(event.getApplication()).thenReturn(EXTERNAL_SYSTEM);
    when(event.getUser()).thenReturn(user);
    doThrow(new UserNotFoundException(USER_ID)).when(userDataManagement).updateDiscord(USER_ID, DISCORD);
    
    // when
    sut.event(event);
    
    // then
    verify(userDataManagement).updateDiscord(USER_ID, DISCORD);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
    log.exit();
  }
  
  @Test
  void shouldIgnoreUpdatingDiscordWhenEventIsFromLocalSystem() {
    log.entry();
    
    // given
    UserDiscordModificationEvent event = mock(UserDiscordModificationEvent.class);
    when(event.getApplication()).thenReturn(LOCAL_SYSTEM);
    
    // when
    sut.event(event);
    
    // then
    verifyNoInteractions(userDataManagement);
    verifyNoInteractions(userManagement);
    verifyNoInteractions(userStateManagement);
    verifyNoInteractions(userRoleManagement);
    verifyNoInteractions(bus);
    
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
