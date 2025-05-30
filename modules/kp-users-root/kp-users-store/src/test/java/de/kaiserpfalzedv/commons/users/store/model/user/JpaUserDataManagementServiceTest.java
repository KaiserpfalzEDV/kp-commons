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
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaUserDataManagementServiceTest {
  
  @InjectMocks
  private JpaUserDataManagementService sut;
  
  @Mock
  private UserRepository repository;
  
  @Mock
  private EventBus bus;
  
  @Mock
  private UserToJpaImpl toJpa;
  
  
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
  void shouldUpdateTheIssuerWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().issuer("new-issuer").subject("new-subject").build());
    
    sut.updateSubject(DEFAULT_ID, "new-issuer", "new-subject");
    
    verify(bus).post(any(UserSubjectModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowExceptionWhenUpdatingTheIssuerWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateSubject(DEFAULT_ID, "new-issuer", "new-subject"));
    
    verify(bus, never()).post(any(UserSubjectModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shoudUpdateNamespaceWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().nameSpace("new-namespace").build());
    
    sut.updateNamespace(DEFAULT_ID, "new-namespace");
    
    verify(bus).post(any(UserNamespaceModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowExceptionWhenUpdatingNamespaceWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateNamespace(DEFAULT_ID, "new-namespace"));
    
    verify(bus, never()).post(any(UserNamespaceModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateNameWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().name("new-name").build());
    
    sut.updateName(DEFAULT_ID, "new-name");
    
    verify(bus).post(any(UserNameModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowExceptionWhenUpdatingNameWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateName(DEFAULT_ID, "new-name"));
    
    verify(bus, never()).post(any(UserNameModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateNamespaceAndNameWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().name("new-name").nameSpace("new-namespace").build());
    
    sut.updateNamespaceAndName(DEFAULT_ID, "new-namespace", "new-name");
    
    verify(bus).post(any(UserNamespaceAndNameModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowExceptionWhenUpdatingNamespaceAndNameWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateNamespaceAndName(DEFAULT_ID, "new-namespace", "new-name"));
    
    verify(bus, never()).post(any(UserNamespaceAndNameModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateEmailWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().email("new-email@email.org").build());
    
    sut.updateEmail(DEFAULT_ID, "new-email@email.org");
    
    verify(bus).post(any(UserEmailModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowAnExceptionWhenUpdatingEmailWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateEmail(DEFAULT_ID, "new-email@email.org"));
    
    verify(bus, never()).post(any(UserEmailModificationEvent.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldUpdateDiscordWhenUserExists() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(DEFAULT_JPA_USER));
    when(repository.saveAndFlush(any(UserJPA.class))).thenReturn(DEFAULT_JPA_USER.toBuilder().discord("new-discord").build());
    
    sut.updateDiscord(DEFAULT_ID, "new-discord");
    
    verify(bus).post(any(UserDiscordModificationEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowAnExceptionWhenUpdatingDiscordWhenUserDoesNotExist() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateDiscord(DEFAULT_ID, "new-discord"));
    
    verify(bus, never()).post(any(UserDiscordModificationEvent.class));
    
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
