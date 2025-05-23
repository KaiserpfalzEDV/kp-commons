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
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
import de.kaiserpfalzedv.commons.users.domain.services.UserDataManagementService;
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
 * Service for managing user data in a JPA context.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserDataManagementService implements UserDataManagementService {
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
  
  
  @Override
  public void updateSubject(final UUID id, final String issuer, final String sub) throws UserNotFoundException {
    log.entry(id, issuer, sub);
    
    UserJPA result = loadUserOrThrowException(id);
    
    result = result.toBuilder()
        .issuer(issuer)
        .subject(sub)
        .build();

    bus.post(UserSubjectModificationEvent.builder().application(system).user(result).build());
    log.exit(repository.saveAndFlush(result));
  }

  
  @Override
  public void updateNamespace(final UUID id, final String namespace) throws UserNotFoundException {
    log.entry(id, namespace);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().nameSpace(namespace).build();
    
    bus.post(UserNamespaceModificationEvent.builder().application(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  
  @Override
  public void updateName(final UUID id, final String name) throws UserNotFoundException {
    log.entry(id, name);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().name(name).build();
    
    bus.post(UserNameModificationEvent.builder().application(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  
  @Override
  public void updateNamespaceAndName(final UUID id, final String namespace, final String name) throws UserNotFoundException {
    log.entry(id, namespace, name);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().nameSpace(namespace).name(name).build();
    
    bus.post(UserNamespaceAndNameModificationEvent.builder().application(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  
  @Override
  public void updateEmail(final UUID id, final String email) throws UserNotFoundException {
    log.entry(id, email);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().email(email).build();
    
    bus.post(UserEmailModificationEvent.builder().application(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }

  
  @Override
  public void updateDiscord(final UUID id, final String discord) throws UserNotFoundException {
    log.entry(id, discord);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().discord(discord).build();
    
    bus.post(UserDiscordModificationEvent.builder().application(system).user(data).build());
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
