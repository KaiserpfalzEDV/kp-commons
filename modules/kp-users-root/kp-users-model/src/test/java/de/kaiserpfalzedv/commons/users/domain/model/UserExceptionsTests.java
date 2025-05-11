/*
 * Copyright (c) 2025.  Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.kaiserpfalzedv.commons.users.domain.model;


import de.kaiserpfalzedv.commons.users.domain.model.user.*;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author klenkes74
 * @since 20.04.25
 */
@XSlf4j
public class UserExceptionsTests {
  
  private static final KpUserDetails USER = KpUserDetails.builder()
      .id(UUID.randomUUID())
      .issuer("https://test.issuer")
      .subject(UUID.randomUUID().toString())
      .nameSpace("test")
      .name("Peter")
      .build();
  
  
  @Test
  public void shouldGiveBannedDateWhenUserIsBanned() {
    log.entry();
    
    User user = USER.toBuilder().bannedOn(OffsetDateTime.now().minusSeconds(1L)).deleted(OffsetDateTime.now().minusSeconds(2L)).build();
    
    UserIsBannedException sut = new UserIsBannedException(user);
    
    assertEquals(user, sut.getUser());
    assertTrue(sut.getBannedAt().isBefore(OffsetDateTime.now()));
    
    log.exit();
  }
  
  
  @Test
  public void shouldGiveDeletedDateWhenUserIsDeleted() {
    log.entry();
    
    User user = USER.toBuilder().deleted(OffsetDateTime.now().minusSeconds(1L)).build();
    
    UserIsDeletedException sut = new UserIsDeletedException(user);
    
    assertEquals(user, sut.getUser());
    assertTrue(sut.getDeletedAt().isBefore(OffsetDateTime.now()));
    
    log.exit();
  }
  
  
  @Test
  public void shouldGiveDetainedInformationWhenUserIsDetained() {
    log.entry();
    
    User user = USER.toBuilder()
        .detainedTill(OffsetDateTime.now().plusDays(20L))
        .detainmentDuration(Duration.ofDays(30L))
        .build();
    
    UserIsDetainedException sut = new UserIsDetainedException(user);
    
    assertEquals(user, sut.getUser());
    assertTrue(sut.getDetainedTill().isAfter(OffsetDateTime.now().plusDays(19L)));
    assertEquals(user.getDetainmentDuration(), sut.getDetainedFor());
    assertEquals(30L, sut.getDetainedDays());
    
    log.exit();
  }
}
