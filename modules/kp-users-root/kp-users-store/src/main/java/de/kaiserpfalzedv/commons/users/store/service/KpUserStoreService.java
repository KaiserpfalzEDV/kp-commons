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

import de.kaiserpfalzedv.commons.users.domain.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import de.kaiserpfalzedv.commons.users.domain.services.UserWriteService;
import de.kaiserpfalzedv.commons.users.store.model.users.UserJPA;
import de.kaiserpfalzedv.commons.users.store.model.users.UserRepository;
import de.kaiserpfalzedv.commons.users.store.model.users.UserToJpa;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class KpUserStoreService implements UserReadService, UserWriteService {
    private final UserRepository users;
    private final UserToJpa toUserJPA;


    @Override
    @Counted
    @Timed
    public Optional<User> findById(final UUID id) {
        log.entry(id);
        
        Optional<UserJPA> result = users.findById(id);
        
        return log.exit(Optional.ofNullable(result.orElse(null)));
    }
    
    @Override
    @Counted
    @Timed
    public Optional<User> findByUsername(final String namespace, final String name) {
        log.entry(namespace, name);
        
        Optional<UserJPA> result = users.findByNameSpaceAndName(namespace, name);
        
        return log.exit(Optional.ofNullable(result.orElse(null)));
    }
    
    @Override
    public Optional<User> findByEmail(final String email) {
        log.entry(email);
        
        Optional<UserJPA> result = users.findByEmail(email);
        
        return log.exit(Optional.ofNullable(result.orElse(null)));
    }
    
    @Override
    public Optional<User> findByOauth(final String issuer, final String sub) {
        log.entry(issuer, sub);
        
        Optional<UserJPA> result = users.findByIssuerAndSubject(issuer, sub);
        
        return log.exit(Optional.ofNullable(result.orElse(null)));
    }
    
    @Override
    public List<User> findAllByNamespace(final String namespace) {
        log.entry(namespace);
        
        List<UserJPA> data = users.findByNameSpace(namespace);
        
        return log.exit(data.stream().map(e -> (User)e).toList());
    }
    
    @Override
    public Page<User> findAllByNamespace(final String namespace, final Pageable pageable) {
        log.entry(namespace, pageable);
        
        Page<UserJPA> result = users.findByNameSpace(namespace, pageable);
        List<User> data = result.stream().map(e -> (User)e).toList();
        
        return log.exit(new PageImpl<>(data, result.getPageable(), result.getTotalElements()));
    }
    
    @Override
    public List<User> findAllByIssuer(final String issuer) {
        log.entry(issuer);
        
        List<UserJPA> data = users.findByIssuer(issuer);
        
        return log.exit(data.stream().map(e -> (User)e).toList());
    }
    
    @Override
    public Page<User> findAllByIssuer(final String issuer, final Pageable pageable) {
        log.entry(issuer, pageable);
        
        Page<UserJPA> result = users.findByIssuer(issuer, pageable);
        List<User> data = result.stream().map(e -> (User)e).toList();
        
        return log.exit(new PageImpl<>(data, result.getPageable(), result.getTotalElements()));
    }
    
    @Override
    public User create(final User user) throws UserCantBeCreatedException {
        log.entry(user);
        
        try {
            UserJPA data = users.save(toUserJPA.apply(user));
            
            return log.exit(data);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new UserCantBeCreatedException(user.getIssuer(), user.getSubject(), user.getUsername(), user.getEmail(), e);
        }
    }
    
    @Override
    public User updateIssuer(final User user, final String issuer, final String sub) {
        log.entry(issuer, sub);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().issuer(issuer).build()));

        return log.exit(users.findById(user.getId()).orElse(null));
    }
    
    @Override
    public User updateNamespace(final User user, final String namespace) {
        log.entry(user, namespace);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().nameSpace(namespace).build()));

        return log.exit(users.findById(user.getId()).orElse(null));
    }
    
    @Override
    public User updateName(final User user, final String name) {
        log.entry(user, name);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().name(name).build()));
        
        return log.exit(users.findById(user.getId()).orElse(null));
    }
    
    @Override
    public User updateNamespaceAndName(final User user, final String namespace, final String name) {
        log.entry(user, namespace, name);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().nameSpace(namespace).name(name).build()));
        
        return log.exit(users.findById(user.getId()).orElse(null));
    }
    
    @Override
    public User updateEmail(final User user, final String email) {
        log.entry(user, email);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().email(email).build()));
        
        return log.exit(users.findById(user.getId()).orElse(null));
    }
    
    @Override
    public User updateDiscord(final User user, final String discord) {
        log.entry(user, discord);
        
        Optional<UserJPA> data = users.findById(user.getId());
        data.ifPresent(e -> users.save(e.toBuilder().discord(discord).build()));
        
        return log.exit(users.findById(user.getId()).orElse(null));
    }
}
