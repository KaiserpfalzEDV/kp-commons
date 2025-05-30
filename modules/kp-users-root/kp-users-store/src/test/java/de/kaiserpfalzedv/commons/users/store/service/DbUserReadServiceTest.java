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


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserReadService;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@Primary
@Order(1000)
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class DbUserReadServiceTest {
  @InjectMocks private DbUserReadService dbUserReadService;
  
  @Mock private JpaUserReadService jpaUserReadService;
  
  @BeforeEach
  void setUp() {
    reset(jpaUserReadService);
  }
  
  @AfterEach
  void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(jpaUserReadService);
  }
  
  
  @Test
  void shouldReturnUserWhenFindByIdIsCalledWithAValidId() {
    log.entry();
    
    // Given
    final UUID id = UUID.randomUUID();
    final UserJPA user = mock(UserJPA.class);
    final Optional<UserJPA> userOptional = Optional.of(user);
    when(jpaUserReadService.findById(id)).thenReturn(userOptional);
    
    // When
    Optional<User> result = dbUserReadService.findById(id);
    
    // Then
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    
    log.exit();
  }
  
  @Test
  void shouldReturnEmptyWhenFindByIdIsCalledWithAnInvalidId() {
    log.entry();
    
    // Given
    final UUID id = UUID.randomUUID();
    when(jpaUserReadService.findById(id)).thenReturn(Optional.empty());
    
    // When
    Optional<User> result = dbUserReadService.findById(id);
    
    // Then
    assertTrue(result.isEmpty());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnUserWhenFindByUsernameIsCalledWithValidParameters() {
    log.entry();
    
    // Given
    final String nameSpace = "testNamespace";
    final String name = "testUser";
    final UserJPA user = mock(UserJPA.class);
    final Optional<UserJPA> userOptional = Optional.of(user);
    when(jpaUserReadService.findByUsername(nameSpace, name)).thenReturn(userOptional);
    
    // When
    Optional<User> result = dbUserReadService.findByUsername(nameSpace, name);
    
    // Then
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    
    log.exit();
  }
  
  @Test
  void shouldReturnEmptyWhenFindByUsernameIsCalledWithAnInvalidParameters() {
    log.entry();
    
    // Given
    final String nameSpace = "testNamespace";
    final String name = "testUser";
    when(jpaUserReadService.findByUsername(nameSpace, name)).thenReturn(Optional.empty());
    
    // When
    Optional<User> result = dbUserReadService.findByUsername(nameSpace, name);
    
    // Then
    assertTrue(result.isEmpty());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnUserWhenFindByOauthIsCalledWithValidParameters() {
    log.entry();
    
    // Given
    final String issuer = "testIssuer";
    final String sub = "testSub";
    final UserJPA user = mock(UserJPA.class);
    final Optional<UserJPA> userOptional = Optional.of(user);
    when(jpaUserReadService.findByOauth(issuer, sub)).thenReturn(userOptional);
    
    // When
    Optional<User> result = dbUserReadService.findByOauth(issuer, sub);
    
    // Then
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    
    log.exit();
  }
  
  @Test
  void shouldReturnEmptyWhenFindByOauthIsCalledWithAnInvalidParameters() {
    log.entry();
    
    // Given
    final String issuer = "testIssuer";
    final String sub = "testSub";
    when(jpaUserReadService.findByOauth(issuer, sub)).thenReturn(Optional.empty());
    
    // When
    Optional<User> result = dbUserReadService.findByOauth(issuer, sub);
    
    // Then
    assertTrue(result.isEmpty());
    
    log.exit();
  }
}
