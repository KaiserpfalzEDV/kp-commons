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

package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
public interface RoleReadService {
  Optional<? extends Role> retrieve(@NotNull UUID id);
  
  List<? extends Role> retrieveByName(@NotNull String name);
  Page<? extends Role> retrieveByName(@NotNull String name, @NotNull Pageable pageable);
  
  List<? extends Role> retrieveAll();
  Page<? extends Role> retrieveAll(@NotNull Pageable pageable);
  
  List<? extends Role> retrieveAllFromNamespace(@NotNull String namespace);
  Page<? extends Role> retrieveAllFromNamespace(@NotNull String namespace, @NotNull Pageable pageable);
}
