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


import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKeyImpl;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKeyNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKeyToImpl;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@SuppressWarnings("LoggingSimilarMessage")
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaApiKeyWriteServiceTest {
  @InjectMocks
  private JpaApiKeyWriteService sut;
  
  @Mock
  private ApiKeyRepository repository;

  @Mock
  private ApiKeyToImpl toImpl;

  @Mock
  private ApiKeyToJPAImpl toJPA;
  
  
  @BeforeEach
  public void setUp() {
    reset(repository, toImpl, toJPA);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage();
    verifyNoMoreInteractions(repository, toImpl, toJPA);
  }
  
  
  @Test
  void shouldSaveApiKeyWhenApiKeyIsNotADuplicate() throws InvalidApiKeyException {
    log.entry();
    
    when(repository.save(DEFAULT_APIKEY)).thenReturn(DEFAULT_APIKEY);
    
    sut.create(DEFAULT_APIKEY);
  }
  
  
  @Test
  void shouldThrowExceptionWhenApiKeyIsADuplicate() {
    log.entry();
    
    when(repository.save(DEFAULT_APIKEY)).thenThrow(new IllegalArgumentException("Duplicate ApiKey"));
    
    assertThrows(InvalidApiKeyException.class, () -> sut.create(DEFAULT_APIKEY));
    
    log.exit();
  }
  
  
  @Test
  void shouldSaveApiKeyWhenApiKeyIsNotJPAType() throws InvalidApiKeyException {
    log.entry();
    
    when(repository.save(any(ApiKeyJPA.class))).thenReturn(DEFAULT_APIKEY);
    when(toJPA.apply(any(ApiKeyImpl.class))).thenReturn(DEFAULT_APIKEY);
    
    sut.create(DEFAULT_APIKEY_IMPL);
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnRefreshedKeyWithNewExpiryDateWhenApiKeyGetsRefreshed() throws ApiKeyNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_APIKEY.getId())).thenReturn(Optional.of(DEFAULT_APIKEY));
    when(repository.save(any(ApiKeyJPA.class))).thenReturn(DEFAULT_APIKEY);
    
    ApiKeyJPA result = sut.refresh(DEFAULT_ID, 20L);
    log.debug("Refreshed ApiKey. apikey={}", result);
    
    assertTrue(result.getExpiration().plusSeconds(1).isAfter(DEFAULT_APIKEY.getExpiration().plusDays(10L)));
    
    log.exit();
  }
  
  
  @Test
  void shouldFailWhileRefreshingWhenApiKeyWithIdDoesNotExist() {
    log.entry();
    
    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
    
    assertThrows(ApiKeyNotFoundException.class, () -> sut.refresh(UUID.randomUUID(), 20L));
    
    log.exit();
  }
  
  
  @Test
  void shouldDeleteApiKeyWhenApiKeyExists() {
    log.entry();
    
    sut.delete(DEFAULT_APIKEY.getId());
    
    verify(repository, times(1)).deleteById(DEFAULT_APIKEY.getId());
    
    log.exit();
  }
  
  
  @Test
  void shouldDeleteApiKeyWhenApiKeyDoesNotExist() {
    log.entry();
    
    sut.delete(UUID.randomUUID());
    
    // repository deleteById ignores if the target does not exist at all.
    verify(repository, times(1)).deleteById(any(UUID.class));
    
    log.exit();
  }
  
  
  @Test
  void shouldRemoveApiKeyWhenApiKeyExists() {
    log.entry();
    
    sut.remove(DEFAULT_APIKEY.getId());

    // remove is mapped to delete to keep the API structured the same way over all objects.
    verify(repository, times(1)).deleteById(DEFAULT_APIKEY.getId());
    
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

      .email("email@email.email")
      
      .version(0)
      .revId(0)
      .revisioned(NOW)
      
      .created(NOW)
      .modified(NOW)
      
      .build();
  
  private static final KpUserDetails DEFAULT_USER_IMPL = KpUserDetails.builder()
      .id(DEFAULT_ID)
      
      .nameSpace("namespace")
      .name("name")
      
      .issuer("issuer")
      .subject(DEFAULT_ID.toString())
      
      .email("email@email.email")
      
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
  
  private static final ApiKeyImpl DEFAULT_APIKEY_IMPL = ApiKeyImpl.builder()
      .id(DEFAULT_ID)
      .expiration(NOW.plusDays(10L))
      
      .nameSpace("namespace")
      .user(DEFAULT_USER_IMPL)
      
      .created(NOW)
      .modified(NOW)
      
      .build();
}
