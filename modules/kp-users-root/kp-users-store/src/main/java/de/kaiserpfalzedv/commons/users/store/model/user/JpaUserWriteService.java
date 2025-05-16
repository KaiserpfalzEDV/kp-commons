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


import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserWriteService;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.*;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserRemovedEvent;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleToJpa;
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
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserWriteService implements UserWriteService {
  private final UserRepository repository;
  private final EventBus bus;
  private final UserToJpa toJpa;
  private final RoleToJpa roleToJpa;
  
  @Value("${spring.application.system:kp-commons}")
  private String system;
  
  
  @Override
  public void create(@NotNull final User user) throws UserCantBeCreatedException {
    log.entry(user);
    
    
    UserJPA result;
    try {
      result = repository.save(toJpa.apply(user));
      
      bus.post(UserCreatedEvent.builder().system(system).user(result).build());
    } catch (OptimisticLockingFailureException e) {
      throw new UserCantBeCreatedException(user, e);
    }
    
    log.exit(result);
  }
  
  @Override
  public void updateIssuer(final UUID id, final String issuer, final String sub) throws UserNotFoundException {
    log.entry(id, issuer, sub);
    
    UserJPA result = loadUserOrThrowException(id);
    
    result = result.toBuilder()
        .issuer(issuer)
        .subject(sub)
        .build();

    bus.post(UserIssuerAndSubModificationEvent.builder().system(system).user(result).build());
    log.exit(repository.saveAndFlush(result));
  }
  
  private UserJPA loadUserOrThrowException(final UUID id) throws UserNotFoundException {
    log.entry(id);
    
    Optional<UserJPA> data = repository.findById(id);
    if (data.isEmpty()) {
      throw log.throwing(new UserNotFoundException(id));
    }
    
    return log.exit(data.get());
  }
  
  @Override
  public void updateNamespace(final UUID id, final String namespace) throws UserNotFoundException {
    log.entry(id, namespace);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().nameSpace(namespace).build();
    
    bus.post(UserNamespaceModificationEvent.builder().system(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void updateName(final UUID id, final String name) throws UserNotFoundException {
    log.entry(id, name);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().name(name).build();
    
    bus.post(UserNameModificationEvent.builder().system(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void updateNamespaceAndName(final UUID id, final String namespace, final String name) throws UserNotFoundException {
    log.entry(id, namespace, name);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().nameSpace(namespace).name(name).build();
    
    bus.post(UserNamespaceAndNameModificationEvent.builder().system(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void updateEmail(final UUID id, final String email) throws UserNotFoundException {
    log.entry(id, email);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().email(email).build();
    
    bus.post(UserEmailModificationEvent.builder().system(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void updateDiscord(final UUID id, final String discord) throws UserNotFoundException {
    log.entry(id, discord);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data = data.toBuilder().discord(discord).build();
    
    bus.post(UserDiscordModificationEvent.builder().system(system).user(data).build());
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void addRole(final UUID id, final Role role) throws UserNotFoundException {
    log.entry(id, role);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data.addRole(bus, roleToJpa.apply(role));
    // no bus post since User does it for us!
    
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void removeRole(final UUID id, final Role role) throws UserNotFoundException {
    log.entry(id, role);
    
    UserJPA data = loadUserOrThrowException(id);
    
    data.removeRole(bus, roleToJpa.apply(role));
    
    log.exit(repository.saveAndFlush(data));
  }
  
  @Override
  public void revokeRoleFromAllUsers(final Role role) {
    log.entry(role);
    
    // TODO 2025-05-16 klenkes74 Implement the role removal.
    throw log.throwing(new UnsupportedOperationException("Revoke role from all users is not implemented yet!"));
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
  
  private void revokeAllApiKeysForUser(final UserJPA user) {
    log.entry(user);
    
    // no system() in this event, since it has to be worked on at least once :-).
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
      
      bus.post(UserRemovedEvent.builder().system(system).user(data).build());
      log.info("User removed from system. user={}", data);
    } catch (UserNotFoundException e) {
      log.info("User didn't exist. Nothing to do. id='{}'", id);
    }
    
    log.exit();
  }
}
