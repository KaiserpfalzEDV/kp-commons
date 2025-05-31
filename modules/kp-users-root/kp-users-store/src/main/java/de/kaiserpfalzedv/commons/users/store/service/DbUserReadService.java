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


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserReadService;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@Primary
@Service
@Order(1000)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class DbUserReadService implements UserReadService<User> {
  private final JpaUserReadService jpa;
  
  @Override
  public Optional<User> findById(@NotNull final UUID id) {
    log.entry(id);
    
    User result = jpa.findById(id).orElse(null);
    
    return log.exit(Optional.ofNullable(result));
  }
  
  @Override
  public List<User> findByNamespace(final String nameSpace) {
    log.entry(nameSpace);
    
    List<UserJPA> result = jpa.findByNamespace(nameSpace);
    
    return log.exit(result.stream().map(u -> (User) u).toList());
  }
  
  @Override
  public Page<User> findByNamespace(final String nameSpace, final Pageable pageable) {
    log.entry(nameSpace, pageable);
    
    Page<UserJPA> result = jpa.findByNamespace(nameSpace, pageable);
    
    if (result == null) {
      result = new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
    
    return log.exit(result.map(u -> u));
  }
  
  @Override
  public List<User> findAll() {
    log.entry();
    
    List<UserJPA> result = jpa.findAll();
    
    if (result == null) {
      result = Collections.emptyList();
    }
    
    return log.exit(result.stream().map(u -> (User) u).toList());
  }
  
  @Override
  public Page<User> findAll(final Pageable pageable) {
    log.entry(pageable);
    
    Page<UserJPA> result = jpa.findAll(pageable);
    
    if (result == null) {
      result = new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
    
    return log.exit(result.map(u -> u));
  }
  
  @Override
  public Optional<User> findByUsername(final String nameSpace, final String name) {
    log.entry(nameSpace, name);
    
    User result = jpa.findByUsername(nameSpace, name).orElse(null);

    return log.exit(Optional.ofNullable(result));
  }
  
  @Override
  public Optional<User> findByOauth(final String issuer, final String sub) {
    log.entry(issuer, sub);
    
    User result = jpa.findByOauth(issuer, sub).orElse(null);
    
    return log.exit(Optional.ofNullable(result));
  }
}
