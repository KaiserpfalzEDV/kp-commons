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
class OffsetDateTimeWritingConverterTest {
  
  private OffsetDateTimeWritingConverter converter;
  
  @BeforeEach
  void setUp() {
    converter = new OffsetDateTimeWritingConverter();
  }
  
  @Test
  void shouldConvertOffsetDateTimeToInstant() {
    log.entry();
    // Arrange
    OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 6, 8, 12, 0, 0, 0, ZoneOffset.ofHours(2));
    
    // Act
    Instant result = converter.convert(offsetDateTime);
    
    // Assert
    assertNotNull(result);
    assertEquals(offsetDateTime.toInstant(), result);
    
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