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


import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@SuppressWarnings("LoggingSimilarMessage")
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaUserReadServiceTest {
  private JpaUserReadService sut;
  
  @Mock
  private UserRepository repository;
  
  
  @BeforeEach
  public void setUp() {
    reset(repository);
    
    sut = new JpaUserReadService(repository);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
  }
  
  
  @Test
  void shouldReturnUserWhenUserWithIdExists() {
    log.entry();
    
    when(repository.findById(DEFAULT_USER.getId())).thenReturn(Optional.of(DEFAULT_USER));
    
    Optional<UserJPA> result = sut.findById(DEFAULT_USER.getId());
    log.debug("Result. user={}", result.orElse(null));
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnUserWhenUserWithUsernameExists() {
    log.entry();
    
    when(repository.findByNameSpaceAndName(DEFAULT_USER.getNameSpace(), DEFAULT_USER.getName())).thenReturn(Optional.of(DEFAULT_USER));
    
    Optional<UserJPA> result = sut.findByUsername(DEFAULT_USER.getNameSpace(), DEFAULT_USER.getName());
    log.debug("Result. user={}", result.orElse(null));
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnUserWhenUserWithSubjectExists() {
    log.entry();
    
    when(repository.findByIssuerAndSubject(DEFAULT_USER.getIssuer(), DEFAULT_USER.getSubject())).thenReturn(Optional.of(DEFAULT_USER));
    
    Optional<UserJPA> result = sut.findByOauth(DEFAULT_USER.getIssuer(), DEFAULT_USER.getSubject());
    log.debug("Result. user={}", result.orElse(null));
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now();
  private static final UserJPA DEFAULT_USER = UserJPA.builder()
      .id(DEFAULT_ID)
      
      .nameSpace("namespace")
      .name("name")
      
      .issuer("issuer")
      .subject(DEFAULT_ID.toString())
      
      .version(0)
      .revId(0)
      .revisioned(CREATED_AT)
      
      .created(CREATED_AT)
      .modified(CREATED_AT)
      
      .build();
}
