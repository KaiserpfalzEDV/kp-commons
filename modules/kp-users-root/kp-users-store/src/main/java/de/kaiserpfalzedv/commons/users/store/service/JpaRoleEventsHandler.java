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

package de.kaiserpfalzedv.commons.users.store.service;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.role.RoleNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.*;
import de.kaiserpfalzedv.commons.users.store.model.role.JpaRoleWriteService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserRoleManagementService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@Service
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class JpaRoleEventsHandler implements RoleEventsHandler, AutoCloseable {
  private final JpaRoleWriteService writeService;
  private final JpaUserRoleManagementService userRoleManagement;
  private final EventBus bus;
  
  @Value("${spring.application.system:kp-commons}")
  private String system = "kp-commons";
  
  
  @PostConstruct
  public void init() {
    log.entry();
    
    bus.register(this);
    
    log.exit();
  }
  
  @Override
  @PreDestroy
  public void close() {
    log.entry();
    
    bus.unregister(this);
    
    log.exit();
  }

  
  @Override
  @EventListener
  public void event(@NotNull final RoleCreatedEvent event) {
    log.entry(event);
    
    if(eventIsFromExternalSystem(event)) {
      try {
        writeService.create(event.getRole());
      } catch (RoleCantBeCreatedException e) {
        log.warn(e.getMessage());
      }
    }
    
    log.exit();
  }
  
  @Override
  @EventListener
  public void event(@NotNull final RoleUpdateNameSpaceEvent event) {
    log.entry(event);
    
    if (eventIsFromExternalSystem(event)) {
      try {
        writeService.updateNameSpace(event.getRole().getId(), event.getRole().getNameSpace());
      } catch (RoleNotFoundException e) {
        log.warn(e.getMessage());
      }
    }
    
    log.exit();
  }
  
  
  @Override
  @EventListener
  public void event(@NotNull final RoleUpdateNameEvent event) {
    log.entry(event);
    
    if (eventIsFromExternalSystem(event)) {
      try {
        writeService.updateName(event.getRole().getId(), event.getRole().getName());
      } catch (RoleNotFoundException e) {
        log.warn(e.getMessage());
      }
    }
    
    log.exit();
  }
  
  
  @Override
  @EventListener
  public void event(@NotNull final RoleRemovedEvent event) {
    log.entry(event);
    
    if (eventIsFromExternalSystem(event)) {
      userRoleManagement.revokeRoleFromAllUsers(event.getRole());
      writeService.remove(event.getRole().getId());
    }
    
    log.exit();
  }
  
  
  /**
   * Check if the event is from an external application.
   * @param event The event to check.
   * @return True if the event is from an external application, false otherwise.
   */
  private boolean eventIsFromExternalSystem(final RoleBaseEvent event) {
    log.entry(event);
    
    boolean result;
    if (system.equals(event.getSystem())) {
      log.debug("System is the same. Ignoring event. event={}", event);
      result = false;
    } else {
      result = true;
    }
    
    return log.exit(result);
  }
}
