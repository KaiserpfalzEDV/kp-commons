/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.events;


import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * @author klenkes74
 * @since 24.11.24
 */
@Getter
public class TestEvent extends BaseEvent {
  private final String i18nKey = "event.test";
  
  public TestEvent(final UUID id) {
    this(id, OffsetDateTime.now());
  }
  
  public TestEvent(final UUID id, final OffsetDateTime timestamp) {
    super(id, timestamp);
  }
  
  public Object[] getI18nData() {
    return new Object[] {};
  }
}
