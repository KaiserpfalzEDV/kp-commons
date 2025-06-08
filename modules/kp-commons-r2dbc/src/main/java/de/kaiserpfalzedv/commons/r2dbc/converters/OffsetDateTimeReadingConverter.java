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


import jakarta.annotation.Nullable;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author klenkes74
 * @since 08.06.25
 */
@ReadingConverter
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class OffsetDateTimeReadingConverter implements Converter<Instant, OffsetDateTime> {
  /**
   * Converts an {@link Instant} to an {@link OffsetDateTime} using the current UTC offset.
   *
   * @param source the Instant to convert
   * @return the converted OffsetDateTime
   * @throws IllegalArgumentException if the source is null
   */
  @Override
  public OffsetDateTime convert(@Nullable final Instant source) {
    log.entry(source);
    
    if (source == null) {
      throw log.throwing(new IllegalArgumentException("Can't convert from null value"));
    }
    
    return log.exit(OffsetDateTime.ofInstant(source, OffsetDateTime.now(ZoneOffset.UTC).getOffset()));
  }
}
