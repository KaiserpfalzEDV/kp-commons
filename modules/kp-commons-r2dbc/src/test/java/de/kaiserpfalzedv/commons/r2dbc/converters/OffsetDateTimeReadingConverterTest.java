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
package de.kaiserpfalzedv.commons.r2dbc.converters;

import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.2.0
 * @since 2025-06-08
 */
@XSlf4j
class OffsetDateTimeReadingConverterTest {

  private OffsetDateTimeReadingConverter converter;

  @BeforeEach
  void setUp() {
    converter = new OffsetDateTimeReadingConverter();
  }

  @Test
  void shouldConvertInstantToOffsetDateTime() {
    log.entry();
    // Arrange
    Instant instant = Instant.parse("2025-06-08T10:00:00Z");

    // Act
    OffsetDateTime result = converter.convert(instant);

    // Assert
    assertNotNull(result);
    assertEquals(instant, result.toInstant());
    assertEquals(ZoneOffset.UTC, result.getOffset());

    log.exit();
  }

  @Test
  void shouldThrowExceptionWhenSourceIsNull() {
    log.entry();
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> converter.convert(null));
    log.exit();
  }
}