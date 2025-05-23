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


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleRemovedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleUpdateNameSpaceEvent;
import de.kaiserpfalzedv.commons.users.domain.services.RoleWriteService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-17
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaRoleWriteService implements RoleWriteService {
  private final RoleRepository repository;
  private final EventBus bus;
  private final RoleToJpaImpl toJpa;
  
  
  @Value("${spring.application.system:kp-commons}")
  private String system = "kp-commons";
  
  
  @PostConstruct
  public void init() {
    log.entry(bus, system);
    
    bus.register(this);
    
    log.exit();
  }
  
  @PreDestroy
  public void close() {
    log.entry(bus, system);
    
    bus.unregister(this);
    
    log.exit();
  }
  
  
  @Timed
  @Counted
  @Override
  public void create(@NotNull final Role role) throws RoleCantBeCreatedException {
    log.entry(role);
    
    try {
      RoleJPA result = repository.saveAndFlush(toJpa.apply(role));
      
      log.info("Created role. nameSpace='{}', name='{}', id={}", result.getNameSpace(), result.getName(), result.getId());
      bus.post(RoleCreatedEvent.builder().system(system).role(result).build());
      
      log.exit(result);
    } catch (OptimisticLockingFailureException e) {
      throw log.throwing(new RoleCantBeCreatedException(role, e));
    }
  }
  
  
  @Timed
  @Counted
  @Override
  public void updateNameSpace(@NotNull final UUID id, @NotNull final String namespace) throws RoleNotFoundException {
    log.entry(id, namespace);
    
    try {
      RoleJPA result = loadRoleOrThrowException(id);
      
      result = result.toBuilder().nameSpace(namespace).build();
      result = repository.saveAndFlush(result);
      
      log.info("Updated role. nameSpace='{}', id={}", result.getNameSpace(), result.getId());
      bus.post(RoleUpdateNameSpaceEvent.builder().system(system).role(result).build());
    } catch (OptimisticLockingFailureException e) {
      throw log.throwing(new IllegalStateException(e.getMessage()));
    }
  }
  
  @Timed
  @Counted
  @Override
  public void updateName(@NotNull final UUID id, @NotNull final String name) throws RoleNotFoundException {
    log.entry(id, name);
    
    try {
      RoleJPA result = loadRoleOrThrowException(id);
      
      result = result.toBuilder().name(name).build();
      result = repository.saveAndFlush(result);
      
      log.info("Updated role. name='{}', id={}", result.getName(), result.getId());
      bus.post(RoleUpdateNameSpaceEvent.builder().system(system).role(result).build());
    } catch (OptimisticLockingFailureException e) {
      throw log.throwing(new IllegalStateException(e.getMessage()));
    }
  }
  
  @Timed
  @Counted
  @Override
  public void remove(@NotNull final UUID id) {
    log.entry(id);
    
    try {
      RoleJPA role = loadRoleOrThrowException(id);
      
      log.info("Removing role. nameSpace='{}', name='{}', id={}", role.getNameSpace(), role.getName(), role.getId());
      repository.delete(role);
      bus.post(RoleRemovedEvent.builder().system(system).role(role).build());
    } catch (RoleNotFoundException e) {
      log.info("Role does not exist. That's what we wanted in the first place. id='{}'", id);
    }
    
    log.exit();
  }
  
  
  private RoleJPA loadRoleOrThrowException(final UUID id) throws RoleNotFoundException {
    log.entry(id);
    
    Optional<RoleJPA> result = repository.findById(id);
    if (result.isEmpty()) {
      throw log.throwing(new RoleNotFoundException(id));
    }
    
    return log.exit(result.get());
  }
  
}
