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

package de.kaiserpfalzedv.commons.users.store.model.role;


import de.kaiserpfalzedv.commons.users.domain.services.RoleReadService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaRoleReadService implements RoleReadService {
  private final RoleRepository repository;
  
  @Override
  @Counted
  @Timed
  public Optional<RoleJPA> retrieve(@NotNull final UUID id) {
    log.entry(id);
    
    return repository.findById(id);
  }
  
  @Override
  public List<RoleJPA> retrieveByName(@NotNull String name) {
    log.entry(name);
    
    return repository.findByName(name);
  }
  
  @Override
  public Page<RoleJPA> retrieveByName(@NotNull String name, @NotNull Pageable pageable) {
    log.entry(name, pageable);
    
    return repository.findByName(name, pageable);
  }
  
  @Override
  @Counted
  @Timed
  public List<RoleJPA> retrieveAll() {
    log.entry();
    
    return repository.findAll();
  }
  
  @Override
  @Counted
  @Timed
  public Page<RoleJPA> retrieveAll(@NotNull final Pageable pageable) {
    log.entry(pageable);
    
    return repository.findAll(pageable);
  }
  
  @Override
  @Counted
  @Timed
  public List<RoleJPA> retrieveAllFromNamespace(@NotBlank final String namespace) {
    log.entry(namespace);
    
    return repository.findByNameSpace(namespace);
  }
  
  @Override
  @Counted
  @Timed
  public Page<RoleJPA> retrieveAllFromNamespace(@NotBlank final String namespace, @NotNull final Pageable pageable) {
    log.entry(namespace, pageable);
    
    return repository.findByNameSpace(namespace, pageable);
  }
}
