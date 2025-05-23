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

package de.kaiserpfalzedv.commons.users.domain.model.user;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class KpUserDetailsTest {
  @Mock
  private EventBus bus;
  
  private KpUserDetails sut;
  
  @BeforeEach
  public void setUp() {
    sut = KpUserDetails.builder()
        .id(DEFAULT_USER_ID)
        .nameSpace(DEFAULT_USER_NAMESPACE)
        .name(DEFAULT_USER_NAME)
        .issuer(DEFAULT_ISSUER)
        .subject(DEFAULT_USER_ID.toString())
        .created(CREATED)
        .modified(CREATED)
        .build();
  }
  
  @Test
  void shouldDetainTheUserWhenUserIsActive() {
    sut.detain(bus, 100L);
    
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDetained());
    assertEquals(Duration.ofDays(100L), sut.getDetainmentDuration());
    assertEquals(calculateEndOfDetainment(100L), sut.getDetainedTill());
  }
  
  private OffsetDateTime calculateEndOfDetainment(long days) {
    return OffsetDateTime.now().plusDays(days).toLocalDate().atStartOfDay().plusDays(1L).atOffset(ZoneOffset.UTC);
  }
  
  @Test
  void shouldPutInNewDetainmentWhenUserIsAlreadyDetained() {
    sut.detain(bus, 100L);
    reset(bus);
    
    sut.detain(bus, 10L);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDetained());
    assertEquals(Duration.ofDays(10L), sut.getDetainmentDuration());
    assertEquals(calculateEndOfDetainment(10L), sut.getDetainedTill());
  }
  
  @Test
  void shouldNotChangedBannedWhenDetainingAUser() {
    sut.ban(bus);
    reset(bus);
    
    sut.detain(bus, 100L);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isBanned());
    assertTrue(sut.isDetained());
  }
  
  @Test
  void shouldNotChangeDeletedWhenDetainingAUser() {
    sut.delete(bus);
    reset(bus);
    
    sut.detain(bus, 100L);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDeleted());
    assertTrue(sut.isDetained());
  }
  
  
  @Test
  void shouldDoNothingWhenReleasingAnActiveUser() {
    sut.release(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isActive());
  }
  
  @Test
  void shouldReleaseABannedUser() {
    sut.ban(bus);
    reset(bus);
    
    sut.release(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isActive());
    assertFalse(sut.isBanned());
  }
  
  @Test
  void shouldReleaseADetainedUser() {
    sut.detain(bus, 100L);
    reset(bus);
    
    sut.release(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isActive());
    assertFalse(sut.isDetained());
  }
  
  @Test
  void shouldReleaseADeletedDetainedUser() {
    sut.delete(bus);
    sut.detain(bus, 100L);
    reset(bus);
    
    sut.release(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDeleted());
    assertFalse(sut.isDetained());
  }
  
  @Test
  void shouldReleaseADeletedBannedUser() {
    sut.delete(bus);
    sut.ban(bus);
    reset(bus);
    
    sut.release(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDeleted());
    assertFalse(sut.isBanned());
  }
  
  
  @Test
  void shouldBanAnActiveUser() {
    sut.ban(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertFalse(sut.isActive());
    assertTrue(sut.isBanned());
  }
  
  @Test
  void shouldBanAnDetainedUser() {
    sut.detain(bus, 100L);
    reset(bus);
    
    sut.ban(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isBanned());
    assertTrue(sut.isDetained());
  }
  
  @Test
  void shouldBanAnBannedUser() {
    sut.ban(bus);
    reset(bus);
    
    sut.ban(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isBanned());
  }
  
  @Test
  void shouldBanAnDeletedUser() {
    sut.delete(bus);
    reset(bus);
    
    sut.ban(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDeleted());
    assertTrue(sut.isBanned());
  }
  
  
  @Test
  void shouldDeleteAnActiveUser() {
    sut.delete(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertFalse(sut.isActive());
    assertTrue(sut.isDeleted());
  }
  
  @Test
  void shouldUndeleteAnActiveUser() {
    sut.undelete(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isActive());
    assertFalse(sut.isDeleted());
  }
  
  @Test
  void shouldUndeleteAnDeletedUser() {
    sut.delete(bus);
    reset(bus);
    
    sut.undelete(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertFalse(sut.isDeleted());
    assertTrue(sut.isActive());
  }
  
  @Test
  void shouldKeepTheDetainedStateWhenUndeletingADetainedUser() {
    sut.detain(bus, 100L);
    sut.delete(bus);
    reset(bus);
    
    sut.undelete(bus);
    verify(bus, times(1)).post(Mockito.any());
    
    assertTrue(sut.isDetained(), "The user should be detained!");
    assertFalse(sut.isDeleted(), "The user should not be deleted anymore!");
  }
  
  @Test
  void shouldKeepTheBannedStateWhenUndeletingABannedUser() {
    sut.ban(bus);
    sut.delete(bus);
    reset(bus);
    
    User result = sut.undelete(bus);
    log.debug("Undeleted a banned user. user={}", result);
    
    verify(bus, times(1)).post(Mockito.any());
    
    assertEquals(result, sut);
    assertTrue(sut.isBanned(), "The user should be banned!");
    assertFalse(sut.isDeleted(), "The user should not be deleted anymore!");
  }
  
  @Test
  void shouldEraseCredentialsWhenAsked() {
    sut.eraseCredentials();
    verify(bus, never()).post(Mockito.any());
  }
  
  private static final UUID DEFAULT_USER_ID = UUID.randomUUID();
  private static final String DEFAULT_ISSUER = "issuer";
  private static final OffsetDateTime CREATED = OffsetDateTime.now();
  private static final String DEFAULT_USER_NAMESPACE = "test";
  private static final String DEFAULT_USER_NAME = "user";
}
