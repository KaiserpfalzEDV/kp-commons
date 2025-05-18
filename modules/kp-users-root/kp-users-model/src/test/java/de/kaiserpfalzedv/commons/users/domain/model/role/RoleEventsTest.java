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

package de.kaiserpfalzedv.commons.users.domain.model.role;


import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleRemovedEvent;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@XSlf4j
public class RoleEventsTest {
  private RoleBaseEvent sut;
  
  private static final String DEFAULT_SYSTEM = "application";
  private static final Role DEFAULT_ROLE = TestRole.builder().build();
  private static final OffsetDateTime DEFAULT_TIMESTAMP = OffsetDateTime.now();
  
  @BeforeEach
  public void setUp() {
    sut = TestRoleEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .system(DEFAULT_SYSTEM)
        .role(DEFAULT_ROLE)
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
    assertEquals(DEFAULT_ROLE.getId(), result[2]);
    assertEquals(DEFAULT_ROLE.getNameSpace(), result[3]);
    assertEquals(DEFAULT_ROLE.getName(), result[4]);
    assertEquals(DEFAULT_ROLE.getAuthority(), result[5]);
    assertEquals(DEFAULT_ROLE.getCreated(), result[6]);
    assertEquals(DEFAULT_ROLE.getModified(), result[7]);
    assertEquals(DEFAULT_ROLE.getDeleted(), result[8]);
  }
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedWithRoleCreatedEvent() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedWithRoleCreatedEvent");
    
    sut = RoleCreatedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .system(DEFAULT_SYSTEM)
        .role(DEFAULT_ROLE)
        .build();

    Object[] result = sut.getI18nData();
    log.debug("Queried i18nData. i18nData={}", result);

    checkDefaultI18nAssertions(result);
    log.exit(result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nDataWhenAskedWithRoleDeletedEvent() {
    log.entry("shouldReturnTheCorrectI18nDataWhenAskedWithRoleDeletedEvent");
    
    sut = RoleRemovedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .system(DEFAULT_SYSTEM)
        .role(DEFAULT_ROLE)
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
    
    assertEquals("role.test", result);
  }
  
  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedWithRoleCreatedEvent() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedWithRoleCreatedEvent");
    
    sut = RoleCreatedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .system(DEFAULT_SYSTEM)
        .role(DEFAULT_ROLE)
        .build();
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("role.created", result);
    log.exit(result);
  }

  
  @Test
  void shouldReturnTheCorrectI18nKeyWhenAskedWithRoleRemovedEvent() {
    log.entry("shouldReturnTheCorrectI18nKeyWhenAskedWithRoleDeletedEvent");
    
    sut = RoleRemovedEvent.builder()
        .timestamp(DEFAULT_TIMESTAMP)
        .system(DEFAULT_SYSTEM)
        .role(DEFAULT_ROLE)
        .build();
    
    String result = sut.getI18nKey();
    log.debug("Queried i18nKey. i18nKey={}", result);
    
    assertEquals("role.removed", result);
    
    log.exit(result);
  }
}
