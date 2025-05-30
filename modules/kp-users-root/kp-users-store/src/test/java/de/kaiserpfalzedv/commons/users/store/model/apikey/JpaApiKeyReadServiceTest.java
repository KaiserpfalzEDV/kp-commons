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

package de.kaiserpfalzedv.commons.users.store.model.apikey;


import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@SuppressWarnings("LoggingSimilarMessage")
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaApiKeyReadServiceTest {
  private JpaApiKeyReadService sut;
  
  @Mock
  private ApiKeyRepository repository;
  
  
  @BeforeEach
  public void setUp() {
    reset(repository);
    
    sut = new JpaApiKeyReadService(repository);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
  }
  
  
  @Test
  void shouldReturnApiKeyWhenApiKeyWithIdExists() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_APIKEY));
    
    Optional<ApiKeyJPA> result = sut.retrieve(DEFAULT_ID);
    log.debug("Result. apikey={}", result);
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnApiKeyWhenApiKeyWithIdAsStringExists() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_APIKEY));
    
    Optional<ApiKeyJPA> result = sut.retrieve(DEFAULT_ID.toString());
    log.debug("Result. apikey={}", result);
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowExceptionWhenApiKeyStringIsNoUUID() {
    log.entry();
    
    assertThrows(IllegalArgumentException.class, () -> sut.retrieve("no-uuid"));
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnListOfApiKeysWhenUserExistsAndHasApiKeys() {
    log.entry();
    
    when(repository.findByUserId(DEFAULT_USER.getId())).thenReturn(List.of(DEFAULT_APIKEY));
    
    List<ApiKeyJPA> result = sut.retrieveForUser(DEFAULT_USER.getId());
    log.debug("Result. apikeys={}", result);
    
    assertEquals(1, result.size());
    
    log.exit();
  }
  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime NOW = OffsetDateTime.now();
  private static final UserJPA DEFAULT_USER = UserJPA.builder()
      .id(DEFAULT_ID)
      
      .nameSpace("namespace")
      .name("name")
      
      .issuer("issuer")
      .subject(DEFAULT_ID.toString())
      
      .version(0)
      .revId(0)
      .revisioned(NOW)
      
      .created(NOW)
      .modified(NOW)
      
      .build();
  
  private static final ApiKeyJPA DEFAULT_APIKEY = ApiKeyJPA.builder()
      .id(DEFAULT_ID)
      .version(0)
      .expiration(NOW.plusDays(10L))
      
      .nameSpace("namespace")
      .user(DEFAULT_USER)
      
      .created(NOW)
      .modified(NOW)
      
      .build();
}
