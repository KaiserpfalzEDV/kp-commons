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

package de.kaiserpfalzedv.commons.users.domain.model.apikey;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsBannedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsDeletedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsDetainedException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.TestUser;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class ApiKeyDefaultMethodsTest {
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final User DEFAULT_USER = TestUser.builder().build();
  private static final OffsetDateTime DEFAULT_EXPIRY_DATE = OffsetDateTime.now().plusDays(1);
  
  private ApiKey sut;
  
  @Mock
  private EventBus bus;
  
  @BeforeEach
  public void setUp() {
    sut = TestApiKey.builder()
        .id(DEFAULT_ID)
        .user(DEFAULT_USER)
        .expiration(DEFAULT_EXPIRY_DATE)
        .build();
  }
  
  
  @Test
  void shouldReturnIdAsName() {
    log.entry("shouldReturnIdAsName");
    
    String result = sut.getName();
    log.debug("querying name. name='{}', id={}", result, DEFAULT_ID);
    
    assertEquals(DEFAULT_ID.toString(), result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldNotBeExpiredWhenAskedForDefaultApiKey() {
    log.entry("shouldNotBeExpiredWhenAskedForDefaultApiKey");
    
    boolean result = sut.isExpired();
    log.debug("querying isExpired. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldNotBeBannedWhenAskedForDefaultApiKey() {
    log.entry("shouldNotBeBannedWhenAskedForDefaultApiKey");
    
    boolean result = sut.isBanned();
    log.debug("querying isBanned. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldNotBeDetainedWhenAskedForDefaultApiKey() {
    log.entry("shouldNotBeDetainedWhenAskedForDefaultApiKey");
    
    boolean result = sut.isDetained();
    log.debug("querying isDetained. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldNotBeDeletedWhenAskedForDefaultApiKey() {
    log.entry("shouldNotBeDeletedWhenAskedForDefaultApiKey");
    
    boolean result = sut.isDeleted();
    log.debug("querying isDeleted. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldNotBeInactiveWhenAskedForDefaultApiKey() {
    log.entry("shouldNotBeInactiveWhenAskedForDefaultApiKey");
    
    boolean result = sut.isInactive();
    log.debug("querying isInactive. result={}", result);
    
    assertFalse(result);
    
    log.exit(result);
  }
  
  @Test
  void shouldThrowNoExceptionWhenCheckedForInactivityForDefaultApiKey() throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException {
    log.entry("shouldThrowNoExceptionWhenCheckedForInactivityForDefaultApiKey");
    
    sut.checkInactive();
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowNoExceptionWhenCheckedForBeingBannedForDefaultApiKey() throws UserIsBannedException {
    log.entry("shouldThrowNoExceptionWhenCheckedForBeingBannedForDefaultApiKey");
    
    sut.checkBanned();
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowNoExceptionWhenCheckedForBeingDeletedAndUserIsNeitherDeletedOrBanned() throws UserIsDeletedException {
    log.entry("shouldThrowNoExeptionWhenCheckedForBeingDeletedForDefaultApiKey");
    
    sut.checkDeleted();
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowNoExceptionWhenCheckedForBeingDeletedAndUserIsBanned() throws UserIsDeletedException {
    log.entry("shouldThrowNoExeptionWhenCheckedForBeingDeletedForDefaultApiKey");
    
    User user = TestUser.builder().build().ban(bus).delete(bus);
    sut = ((TestApiKey)sut).toBuilder().user(user).build();
    
    sut.checkDeleted();
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowExceptionWhenCheckedForBeingDeletedAndUserIsNotBanned() {
    log.entry("shouldThrowNoExeptionWhenCheckedForBeingDeletedForDefaultApiKey");
    
    User user = TestUser.builder().build().delete(bus);
    
    sut = ((TestApiKey)sut).toBuilder().user(user).build();
    log.trace("Deleted user. user={}", user);
    
    assertThrows(UserIsDeletedException.class, () -> sut.checkDeleted());
    
    log.exit();
  }
  
  
  @Test
  void shouldThrowNoExceptionWhenCheckedForBeingDetainedForDefaultApiKey() throws UserIsDetainedException {
    log.entry("shouldThrowNoExceptionWhenCheckedForBeingDetainedForDefaultApiKey");
    
    sut.checkDetained();
    
    log.exit();
  }
  
  
  @Test
  void shouldGenerateRevokeEventWhenApiKeyGetsRevoked() {
    log.entry("shouldGenerateRevokeEventWhenApiKeyGetsRevoked");
    
    sut.revoke(bus);
    verify(bus).post(any(ApiKeyRevokedEvent.class));
    
    log.exit();
  }
}
