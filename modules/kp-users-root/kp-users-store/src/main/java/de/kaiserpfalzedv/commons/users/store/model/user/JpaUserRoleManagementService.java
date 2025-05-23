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

package de.kaiserpfalzedv.commons.users.store.model.user;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.services.RoleReadService;
import de.kaiserpfalzedv.commons.users.domain.services.UserRoleManagementService;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * Service for managing user roles in a JPA context.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserRoleManagementService implements UserRoleManagementService, AutoCloseable {
  private final UserRepository repository;
  private final EventBus bus;
  private final RoleReadService roleReadService;
  
  
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
  
  
  @Override
  public void addRole(final UUID id, final Role role) throws UserNotFoundException, RoleNotFoundException {
    log.entry(id, role);
    
    UserJPA user = loadUserOrThrowException(id);
    RoleJPA jpa = loadRoleOrThrowException(role.getId());
    
    user.addRole(bus, jpa);
    // no bus post since User does it for us!
    
    log.exit(repository.saveAndFlush(user));
  }
  
  @Override
  public void removeRole(final UUID id, final Role role) throws UserNotFoundException, RoleNotFoundException {
    log.entry(id, role);
    
    UserJPA user = loadUserOrThrowException(id);
    RoleJPA jpa = loadRoleOrThrowException(role.getId());
    
    user.removeRole(bus, jpa);
    
    log.exit(repository.saveAndFlush(user));
  }
  
  @Override
  public void revokeRoleFromAllUsers(final Role role) {
    log.entry(role);
    
    // TODO 2025-05-16 klenkes74 Implement the role removal.
    throw log.throwing(new UnsupportedOperationException("Revoke role from all users is not implemented yet!"));
  }
  

  /**
   * Loads the user from the repository or throws an exception if not found.
   *
   * @param id The ID of the user to load.
   * @return The loaded user.
   * @throws UserNotFoundException If the user is not found.
   */
  private UserJPA loadUserOrThrowException(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    Optional<UserJPA> data = repository.findById(id);
    if (data.isEmpty()) {
      throw log.throwing(new UserNotFoundException(id));
    }
    
    return log.exit(data.get());
  }
  
  private RoleJPA loadRoleOrThrowException(final UUID id) throws RoleNotFoundException {
    log.entry(id);
    
    Optional<? extends Role> data = roleReadService.retrieve(id);
    if (data.isEmpty()) {
      throw log.throwing(new RoleNotFoundException(id));
    }
    
    return log.exit((RoleJPA)data.get());
  }
}
