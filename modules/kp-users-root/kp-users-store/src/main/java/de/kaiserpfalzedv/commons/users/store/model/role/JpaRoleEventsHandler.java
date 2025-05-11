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


import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleDeletedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleEventsHandler;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserWriteService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@Service
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class JpaRoleEventsHandler implements RoleEventsHandler {
  private final RoleRepository repository;
  private final UserWriteService users;
  
  private final RoleToJpa toJPA;
  
  @Override
  public void event(final RoleCreatedEvent event) {
    log.entry(event);
    
    RoleJPA saved = repository.saveAndFlush(toJPA.apply(event.getRole()));
    log.info("Saved new created role. role={}, event.role={}", saved, event.getRole());
    
    log.exit(event);
  }
  
  @Override
  public void event(final RoleDeletedEvent event) {
    log.entry(event);
    
    users.revokeRoleFromAllUsers(event.getRole());
    repository.deleteById(event.getRole().getId());
    log.info("Deleted role. role={}", event.getRole());
    
    log.exit(event);
  }
}
