/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package de.kaiserpfalzedv.commons.users.client.service;

import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 *  This service reports all user login event to the AMQP queue for user logins.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.1.0
 * @since 2024-11-05
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserLoggedInStateRepository {
  public static final int INACTIVITY_LIMIT_IN_SECONDS = 3600;
  
  /** The cache of logged-in user. */
  private final HashMap<User, Instant> lastLogin = new HashMap<>();

  
  @PostConstruct
  public void init() {
    log.exit();
  }

  
  @PreDestroy
  public void shutdown() {
    log.entry();
    
    synchronized (lastLogin) {
      log.trace("Clearing last login cache.");
      lastLogin.clear();
    }
    
    log.exit();
  }
  
  /**
   * Checks if a user is logged in.
   * @param user The user to check
   * @return true if the user is already logged in.
   */
  public boolean isLoggedIn(User user) {
    log.entry(user);
    
    boolean result = false;
    
    if (lastLogin.containsKey(user)) {
      result = true;
      
      if (lastLogin.get(user).isBefore(Instant.now().minusSeconds(INACTIVITY_LIMIT_IN_SECONDS))) {
        synchronized (lastLogin) {
          lastLogin.remove(user);
          log.info("User locally logged out due to inactivity. user={}", user);
        }
        result = false;
      }
    }
    
    return log.exit(result);
  }

  
  @Timed
  public void login(final User user) {
    log.entry(user);
    
    synchronized (lastLogin) {
      lastLogin.put(user, Instant.now());
    }
    log.debug("User is now logged in or has their activity marker updated. user={}", user);
    
    log.exit();
  }
  
  @Timed
  public void login(final User user, final Instant lastLoginTime) {
    log.entry(user, lastLoginTime);
    
    synchronized (lastLogin) {
      lastLogin.put(user, lastLoginTime);
    }
    log.debug("User is logged in with specified login time. user={}, loginTime={}", user, OffsetDateTime.from(lastLoginTime.atZone(ZoneId.systemDefault())));
  }
  
  @Timed
  public void logout(final User user) {
    log.entry(user);
    
    synchronized (lastLogin) {
      log.debug("User logged out. user={}", user);
      lastLogin.remove(user);
    }

    log.exit();
  }
  
  @Timed
  @Scheduled(initialDelay = 20, fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
  public void purgeInactiveUsers() {
    log.entry();
    
    int oldSize;
    
    synchronized(lastLogin) {
      oldSize = lastLogin.size();
      
      Map<User, Instant> result = lastLogin.entrySet().stream()
          .filter((e) -> e.getValue().isBefore(Instant.now().minusSeconds(INACTIVITY_LIMIT_IN_SECONDS)))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      
      result.forEach((user, instant) -> {
        log.info("User locally logged out due to inactivity. user={}, lastActivity={}", user, OffsetDateTime.ofInstant(instant, ZoneId.of("UTC")));
        lastLogin.remove(user);
      });
    }

    log.debug("Purging user login cache. old={}, new={}", oldSize, lastLogin.size());
    
    log.exit();
  }
  
  public void purgeAllUsers() {
    synchronized (lastLogin) {
      lastLogin.clear();
      log.warn("Purged all user from cache.");
    }
  }
}
