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
package de.kaiserpfalzedv.commons.users.domain.model.role.events;


import de.kaiserpfalzedv.commons.api.events.BaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-11-05
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class RoleBaseEvent extends BaseEvent {
  
  @ToString.Include
  final private String system;
  
  @ToString.Include
  final private Role role;
  
  @Override
  public  Object[] getI18nData() {
    return new Object[] {
        getTimestamp(),
        system,
        role.getId(),
        role.getNameSpace(),
        role.getName(),
        role.getAuthority(),
        role.getCreated(),
        role.getModified(),
        role.getDeleted()
    };
  }
}
