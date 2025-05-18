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


import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyNearExpiryEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.TestUser;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@XSlf4j
public class ApiKeyEventsTest {
  private ApiKeyBaseEvent sut;
  
  private static final String DEFAULT_SYSTEM = "application";
  private static final User DEFAULT_USER = TestUser.builder().build();
  private static final OffsetDateTime DEFAULT_TIMESTAMP = OffsetDateTime.now();
  private static final ApiKey DEFAULT_APIKEY = TestApiKey.builder().user(DEFAULT_USER).build();
  
  @BeforeEach
  public void setUp() {
    sut = TestApiKeyEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
  }
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedForIt() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedForIt");
    
    Object[] result = sut.getI18nData();
    log.debug("Queried i18nData. i18nData={}", result);
    
    checkDefaultI18nAssertions(result);
  }
  
  private static void checkDefaultI18nAssertions(final Object[] result) {
    assertEquals(9, result.length);
    assertEquals(DEFAULT_TIMESTAMP, result[0]);
    assertEquals(DEFAULT_SYSTEM, result[1]);
    assertEquals(DEFAULT_APIKEY.getNameSpace(), result[2]);
    assertEquals(DEFAULT_APIKEY.getName(), result[3]);
    assertEquals(DEFAULT_APIKEY.getExpiration(), result[4]);
    // Only checking for seconds - otherwise it would be too hard to get it right for µs ...
    assertEquals(Duration.between(OffsetDateTime.now(), DEFAULT_APIKEY.getExpiration()).toSeconds(), ((Duration)result[5]).toSeconds());
    assertEquals(DEFAULT_USER.getId(), result[6]);
    assertEquals(DEFAULT_USER.getNameSpace(), result[7]);
    assertEquals(DEFAULT_USER.getName(), result[8]);
  }
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyCreatedEvent() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyCreatedEvent");
    
    sut = ApiKeyCreatedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();

    Object[] result = sut.getI18nData();
    log.debug("Queried i18nData. i18nData={}", result);

    checkDefaultI18nAssertions(result);
    log.exit(result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyDeletedEvent() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyDeletedEvent");
    
    sut = ApiKeyRevokedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
    
    Object[] result = sut.getI18nData();
    log.debug("Queried i18nData. i18nData={}", result);
    
    checkDefaultI18nAssertions(result);
    log.exit(result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyNearExpiryEvent() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedWithApiKeyNearExpiryEvent");
    
    sut = ApiKeyNearExpiryEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
    
    Object[] result = sut.getI18nData();
    log.debug("Queried i18nData. i18nData={}", result);
    
    checkDefaultI18nAssertions(result);
    log.exit(result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedForIt() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedForIt");
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("apikey.test", result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedWithApikeyCreatedEvent() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedWithApikeyCreatedEvent");
    
    sut = ApiKeyCreatedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("user.api-key.created", result);
    log.exit(result);
  }

  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedWithApiKeyRevokedEvent() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedWithApiKeyRevokedEvent");
    
    sut = ApiKeyRevokedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("user.api-key.revoked", result);
    
    log.exit(result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedWithApiKeyNearExpiryEvent() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedWithApiKeyNearExpiryEvent");
    
    sut = ApiKeyNearExpiryEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .application(DEFAULT_SYSTEM)
        .user(DEFAULT_USER)
        .apiKey(DEFAULT_APIKEY)
        .build();
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("user.api-key.expiry", result);
    
    log.exit(result);
  }
}

