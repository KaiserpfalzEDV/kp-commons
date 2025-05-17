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


import com.google.common.eventbus.Subscribe;
import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyEventsHandler;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyNearExpiryEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.store.model.apikey.JpaApiKeyWriteService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@Service
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class JpaApiKeyEventsHandler implements ApiKeyEventsHandler, AutoCloseable {
  private final JpaApiKeyWriteService writeService;
  private final LoggingEventBus bus;
  
  
  @Value("${spring.application.system:kp-commons-users}")
  private String system;

  
  @PostConstruct
  public void init() {
    log.entry(bus, system);
    
    bus.register(this);
    
    log.exit();
  }

  @Override
  @PreDestroy
  public void close() {
    log.entry(bus, system);
    
    bus.unregister(this);
    
    log.exit();
  }

  
  @Override
  @Subscribe
  public void event(@NotNull final ApiKeyCreatedEvent event) {
    log.entry(event);
    
    try {
      writeService.create(event.getApiKey());
    } catch (InvalidApiKeyException e) {
      log.warn(e.getMessage(), e);
    }
    
    log.exit();
  }
  
  @Override
  @Subscribe
  public void event(@NotNull final ApiKeyRevokedEvent event) {
    log.entry(event);
    
    writeService.delete(event.getApiKey().getId());

    log.exit();
  }
  
  @Override
  @Subscribe
  public void event(@NotNull final ApiKeyNearExpiryEvent event) {
    // Don't need to handle this event at all.
    log.trace("Nothing to do.");
  }
}
