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


import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserEventsHandler;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLogoutEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitation.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import de.kaiserpfalzedv.commons.users.store.model.user.UserRepository;
import de.kaiserpfalzedv.commons.users.store.model.user.UserToJpaImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-04-19
 */
@Service
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class JpaUserEventsHandler implements UserEventsHandler {
  private final LoggingEventBus bus;
  private final UserRepository repository;
  private final UserToJpaImpl toJpa;
  
  @PostConstruct
  public void init() {
    log.entry(bus);
    
    bus.register(this);
    
    log.exit();
  }
  
  @PreDestroy
  public void destroy() {
    log.entry(bus);
    
    bus.unregister(this);
    
    log.exit();
  }

  
  @Override
  public void event(final UserActivatedEvent event) {
    log.entry(event);

    Optional<UserJPA> data = repository.findById(event.getUser().getId());
    
    data.ifPresent(userJPA -> userJPA.undelete(bus));
    
    log.exit();
  }
  
  @Override
  public void event(final UserCreatedEvent event) {
    log.entry(event);

    UserJPA result = repository.save(toJpa.apply(event.getUser()));
    log.info("Created new user. stored={}", result);
    
    log.exit(result);
  }
  
  @Override
  public void event(final UserDeletedEvent event) {
    log.entry(event);

    Optional<UserJPA> data = repository.findById(event.getUser().getId());
    data.ifPresent(u -> {
      u.delete(bus);
      repository.save(u);
      log.info("Deleted user. user={}", u);
    });
    
    log.exit(data.orElse(null));
  }
  
  @Override
  public void event(final UserRemovedEvent event) {
    log.entry(event);

    repository.deleteById(event.getUser().getId());
    log.info("Removed user by ID. user={}", event.getUser());
    
    log.exit(event.getUser());
  }
  
  @Override
  public void event(final UserBannedEvent event) {
    log.entry(event);

    Optional<UserJPA> data = repository.findById(event.getUser().getId());
    data.ifPresent(u -> {
      u.ban(bus);
      repository.save(u);
      log.info("Banned user. user={}", u);
    });
    
    log.exit(data.orElse(null));
  }
  
  @Override
  public void event(final UserDetainedEvent event) {
    log.entry(event);
    
    Optional<UserJPA> data = repository.findById(event.getUser().getId());
    data.ifPresent(u -> {
      u.detain(bus, event.getDays());
      repository.save(u);
      log.info("Detained user. user={}, days={}, till={}", u, u.getDetainmentDuration(), u.getDetainedTill());
    });
    
    log.exit(data.orElse(null));
  }
  
  @Override
  public void event(final UserPetitionedEvent event) {
    log.entry(event);

    log.warn("user petitioned event not yet implemented!");
    
    log.exit();
  }
  
  @Override
  public void event(final UserReleasedEvent event) {
    log.entry(event);

    Optional<UserJPA> data = repository.findById(event.getUser().getId());
    data.ifPresent(u -> {
      u.release(bus);
      repository.save(u);
      log.info("Released user. user={}", u);
    });
    
    log.exit();
  }
  
  @Override
  public void event(final UserLoginEvent event) {
    log.entry(event);
    
    // TODO 2025-05-10 klenkes74 Implement a user log database.
    log.info("User logged in. user={}", event.getUser());

    log.exit();
  }
  
  @Override
  public void event(final UserLogoutEvent event) {
    log.entry(event);
    
    // TODO 2025-05-10 klenkes74 Implement a user log database.
    log.info("User logged out. user={}", event.getUser());
    
    log.exit();
  }
}
