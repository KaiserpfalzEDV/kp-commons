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
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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
