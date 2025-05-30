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
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.services.UserStateManagementService;
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
 * Handles user state changes and updates the user repository accordingly.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserStateManagementService implements UserStateManagementService {
  private final UserRepository repository;
  private final EventBus bus;
 
  
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
  
  
  
  @SuppressWarnings("removal")
  @Override
  public void activate(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    UserJPA data = loadUserOrThrowException(id);
    data.undelete(bus);
    
    log.exit(repository.saveAndFlush(data));
    
  }
  
  @Override
  public void detain(final UUID id, final long days) throws UserNotFoundException {
    log.entry(id, days);
    
    UserJPA data = loadUserOrThrowException(id);
    data.detain(bus, days);
    
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void ban(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    UserJPA data = loadUserOrThrowException(id);
    data.ban(bus);
    
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void release(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    UserJPA data = loadUserOrThrowException(id);
    data.release(bus);
    
    log.exit(repository.saveAndFlush(data));
  }
  
  
  private UserJPA loadUserOrThrowException(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    Optional<UserJPA> data = repository.findById(id);
    if (data.isEmpty()) {
      throw log.throwing(new UserNotFoundException(id));
    }
    
    return log.exit(data.get());
  }
}
