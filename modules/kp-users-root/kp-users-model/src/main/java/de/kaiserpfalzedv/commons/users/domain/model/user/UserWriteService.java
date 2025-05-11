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

package de.kaiserpfalzedv.commons.users.domain.model.user;


import de.kaiserpfalzedv.commons.users.domain.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
public interface UserWriteService {
  void create(@NotNull User user) throws UserCantBeCreatedException;
  
  void updateIssuer(@NotNull UUID id, @NotBlank String issuer, @NotBlank String sub) throws UserNotFoundException;
  void updateNamespace(@NotNull UUID id, @NotBlank String namespace) throws UserNotFoundException;
  void updateName(@NotNull UUID id, @NotBlank String name) throws UserNotFoundException;
  void updateNamespaceAndName(@NotNull UUID id, @NotBlank String namespace, @NotBlank String name) throws UserNotFoundException;
  void updateEmail(@NotNull UUID id, @NotBlank String email) throws UserNotFoundException;
  void updateDiscord(@NotNull UUID id, @NotBlank String discord) throws UserNotFoundException;
  void addRole(@NotNull UUID id, @NotNull Role role) throws UserNotFoundException;
  void removeRole(@NotNull UUID id, @NotNull Role role) throws UserNotFoundException;
  void revokeRoleFromAllUsers(@NotNull Role role);
  
  void delete(@NotNull UUID id);
  void remove(@NotNull UUID id);
}
