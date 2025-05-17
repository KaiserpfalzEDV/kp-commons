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


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-17
 */
@Getter
public class RoleCantBeCreatedException extends BaseRoleException {
  public RoleCantBeCreatedException(@NotNull final Role role, @Nullable final Throwable cause) {
    super(role, createMessage(role), cause);
  }
  
  private static String createMessage(@NotNull final Role role) {
    return "Role '%s/%s' with id '%s' can't be created".formatted(
        role.getNameSpace(),
        role.getName(),
        role.getId()
    );
  }
}
