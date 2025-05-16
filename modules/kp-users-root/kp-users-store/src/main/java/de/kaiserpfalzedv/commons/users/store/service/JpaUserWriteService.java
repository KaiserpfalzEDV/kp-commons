/*
 * Copyright (c) 2024-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserWriteService;
import de.kaiserpfalzedv.commons.users.store.model.role.JpaRoleReadService;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import de.kaiserpfalzedv.commons.users.store.model.user.UserRepository;
import de.kaiserpfalzedv.commons.users.store.model.user.UserToJpa;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.ext.XLogger;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserWriteService implements UserWriteService {
    private final UserRepository users;
    private final UserToJpa toUserJPA;

    private final JpaRoleReadService rolesReader;
  
  private final EventBus bus;
    
    @Override
    public void create(final User user) throws UserCantBeCreatedException {
        log.entry(user);
        
        try {
            UserJPA data = users.save(toUserJPA.apply(user));
            
            log.exit(data);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new UserCantBeCreatedException(user.getIssuer(), user.getSubject(), user.getUsername(), user.getEmail(), e);
        }
    }
    
    @Override
    public void updateIssuer(final UUID id, final String issuer, final String sub) throws UserNotFoundException {
        log.entry(issuer, sub);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            UserJPA saved = users.save(u.toBuilder().issuer(issuer).subject(sub).build());
            log.info("User updated with new issuer and sub. user={}, issuer={}, sub={}", id, saved.getIssuer(), saved.getSubject());
        });
        
        log.exit();
    }
    
    private void checkForUser(final UUID id, Optional<UserJPA> data) throws UserNotFoundException {
        log.entry(id, data);
        
        if (data.isEmpty()) {
            throw log.throwing(XLogger.Level.WARN, new UserNotFoundException(id));
        }
        
        log.exit(id);
    }
    
    @Override
    public void updateNamespace(final UUID id, final String namespace) throws UserNotFoundException {
        log.entry(id, namespace);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            users.save(u.toBuilder().nameSpace(namespace).build());
            log.info("User updated with new namespace. user={}, namespace={}", id, namespace);
        });

        log.exit();
    }
    
    @Override
    public void updateName(final UUID id, final String name) throws UserNotFoundException {
        log.entry(id, name);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            users.save(u.toBuilder().name(name).build());
            log.info("User updated with new name. user={}, name={}", id, name);
        });
        
        log.exit();
    }
    
    @Override
    public void updateNamespaceAndName(final UUID id, final String namespace, final String name) throws UserNotFoundException {
        log.entry(id, namespace, name);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            users.save(u.toBuilder().nameSpace(namespace).name(name).build());
            log.info("User updated with new namespace and name. user={}, namespace={}, name={}", id, namespace, name);
        });
        
        log.exit();
    }
    
    @Override
    public void updateEmail(final UUID id, final String email) throws UserNotFoundException {
        log.entry(id, email);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            users.save(u.toBuilder().email(email).build());
            log.info("User updated with new email. user={}, email={}", id, email);
        });
        
        log.exit();
    }
    
    @Override
    public void updateDiscord(final UUID id, final String discord) throws UserNotFoundException {
        log.entry(id, discord);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id,data);
        
        data.ifPresent(u -> {
            users.save(u.toBuilder().discord(discord).build());
            log.info("User updated with new discord. user={}, discord={}", id, discord);
        });
        
        log.exit();
    }
    
    @Override
    public void addRole(final UUID id, final Role role) throws UserNotFoundException {
        log.entry(id, role);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            Optional<RoleJPA> roleData = rolesReader.retrieve(role.getId());
            roleData.ifPresentOrElse(
                r -> {
                    u.addRole(bus, r);
                    users.save(u);
                    log.info("User updated with new role. user={}, role={}", id, role);
                },
                () -> log.warn("There is no such role to be added to user. user={}, role={}", id, role)
            );
        });
        
        log.exit();
    }
    
    @Override
    public void removeRole(final UUID id, final Role role) throws UserNotFoundException {
        log.entry(id, role);
        
        Optional<UserJPA> data = users.findById(id);
        checkForUser(id, data);
        
        data.ifPresent(u -> {
            Optional<RoleJPA> roleData = rolesReader.retrieve(role.getId());
            roleData.ifPresentOrElse(
                r -> removeRoleFromUser(data.get(), r),
                () -> log.warn("There is no such role to be removed from user. user={}, role={}", id, role)
            );
        });
        
        log.exit();
    }
    
    @Override
    public void revokeRoleFromAllUsers(final Role role) {
        Optional<RoleJPA> roleJPA = rolesReader.retrieve(role.getId());
        if (roleJPA.isEmpty()) {
            log.warn("There is no such role. Nothing to do. role={}", role);
            return;
        }
        
        List<UserJPA> data = users.findByAuthoritiesContains(Set.of(roleJPA.get()));
        data.forEach(u -> removeRoleFromUser(u, roleJPA.get()));
 
        
    }
    
    private void removeRoleFromUser(final UserJPA user, final RoleJPA role) {
        log.entry(user, role);

        user.removeRole(bus, role);
        log.info("Removed role from user. user={}, role.removed='{}', role.remaining={}", user.getId(), role.getName(), user.getAuthorities());
        users.save(user);
        
        log.exit();
    }
    
    @Override
    public void delete(final UUID id) {
        log.entry(id);

        Optional<UserJPA> data = users.findById(id);
        data.ifPresent(u -> {
            u.delete(bus);
            users.save(u);
            
            log.info("User marked as deleted. user={}", id);
        });
        
        log.exit();
    }
    
    @Override
    public void remove(final UUID id) {
        log.entry(id);
        
        users.deleteById(id);
        log.info("User has been finally removed. user={}", id);
        
        log.exit();
    }
}
