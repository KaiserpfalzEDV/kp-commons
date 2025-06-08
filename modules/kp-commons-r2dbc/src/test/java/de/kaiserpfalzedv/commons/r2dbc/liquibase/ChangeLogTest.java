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

package de.kaiserpfalzedv.commons.r2dbc.liquibase;


import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author klenkes74
 * @since 08.06.25
 */
@XSlf4j
public class ChangeLogTest {
  private ChangeLog sut;
  
  @BeforeEach
  public void setUp() {
    sut = DEFAULT.toBuilder().build(); // clone the default
  }
  
  @Test
  public void shouldHaveTagWhenCheckingTheDefault() {
    log.entry(sut);
    
    assertEquals(DEFAULT.getTag(), sut.getTag());
  }
  
  private static final ChangeLog DEFAULT = ChangeLog.builder()
      .id("test-id")
      .author("test-author")
      .filename("test-filename.xml")
      .executionOrder(1)
      .executionType(ChangeLog.ExecType.EXECUTED)
      .md5Sum("test-md5sum")
      .description("Test description")
      .comments("Test comments")
      .tag("test-tag")
      .liquibaseVersion("4.10.0")
      .executionDate(OffsetDateTime.now().minusMonths(2L))
      .contexts("test-contexts")
      .build();
}
