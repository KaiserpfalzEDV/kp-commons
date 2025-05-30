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
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserRemovedEvent;
import de.kaiserpfalzedv.commons.users.domain.services.UserManagementService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * Service for managing users in a JPA context.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserManagementService implements UserManagementService {
  private final UserRepository repository;
  private final EventBus bus;
  private final UserToJpaImpl toJpa;
  
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
  public void create(@NotNull final User user) throws UserCantBeCreatedException {
    log.entry(user);
    
    try {
      UserJPA result = repository.save(toJpa.apply(user));
      
      bus.post(UserCreatedEvent.builder().application(system).user(result).build());
      log.exit(result);
    } catch (OptimisticLockingFailureException e) {
      throw log.throwing(new UserCantBeCreatedException(user, e));
    }
  }
  
  
  @Override
  public void delete(final UUID id) {
    log.entry(id);
    
    try {
      UserJPA data = loadUserOrThrowException(id);
      data.delete(bus);
      
      revokeAllApiKeysForUser(data);
      
      log.exit(repository.saveAndFlush(data));
    } catch (UserNotFoundException e) {
      log.info("User didn't exist. Nothing to do. id='{}'", id);
      
      log.exit();
    }
  }
  
  @Override
  public void undelete(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    UserJPA data = loadUserOrThrowException(id);
    data.undelete(bus);
    
    log.exit(repository.saveAndFlush(data));
  }
  
  private void revokeAllApiKeysForUser(final UserJPA user) {
    log.entry(user);
    
    // no application() in this event, since it has to be worked on at least once :-).
    user.getApiKeys().forEach(key -> bus.post(ApiKeyRevokedEvent.builder().user(user).apiKey(key).build()));
    
    log.exit(user);
  }
  
  
  @Override
  public void remove(final UUID id) {
    log.entry(id);
    
    try {
      UserJPA data = loadUserOrThrowException(id);
      
      revokeAllApiKeysForUser(data);
      
      repository.delete(data);
      
      bus.post(UserRemovedEvent.builder().application(system).user(data).build());
      log.info("User removed from application. user={}", data);
    } catch (UserNotFoundException e) {
      log.info("User didn't exist. Nothing to do. id='{}'", id);
    }
    
    log.exit();
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
