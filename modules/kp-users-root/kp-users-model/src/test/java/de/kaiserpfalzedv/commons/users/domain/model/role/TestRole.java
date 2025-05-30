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


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "name"})
public class TestRole implements Role {
  @Builder.Default
  private UUID id = UUID.randomUUID();
  @Builder.Default
  private String nameSpace = "nameSpace";
  @Builder.Default
  private String name = "NAME";
  
  @Override
  public OffsetDateTime getCreated() {
    return null;
  }
  
  @Override
  public OffsetDateTime getModified() {
    return null;
  }
  
  @Override
  public OffsetDateTime getDeleted() {
    return null;
  }
}