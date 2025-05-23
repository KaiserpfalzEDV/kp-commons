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


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.*;
import de.kaiserpfalzedv.commons.users.store.model.apikey.JpaApiKeyWriteService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
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
  private final EventBus bus;
  
  @Value("${spring.application.system:kp-commons}")
  private String system = "kp-commons";

  
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
  @EventListener
  public void event(@NotNull final ApiKeyCreatedEvent event) {
    log.entry(event);
    
    if (eventIsFromExternalSystem(event)) {
      try {
        writeService.create(event.getApiKey());
      } catch (InvalidApiKeyException e) {
        log.warn(e.getMessage(), e);
      }
    }
    
    log.exit();
  }
  
  @Override
  @EventListener
  public void event(@NotNull final ApiKeyRevokedEvent event) {
    log.entry(event);
    
    if (eventIsFromExternalSystem(event)) {
      writeService.delete(event.getApiKey().getId());
    }

    log.exit();
  }
  
  @Override
  @EventListener
  public void event(@NotNull final ApiKeyNearExpiryEvent event) {
    // Don't need to handle this event at all.
    log.trace("Nothing to do.");
  }
  
  
  /**
   * Check if the event is from an external application.
   * @param event The event to check.
   * @return True if the event is from an external application, false otherwise.
   */
  private boolean eventIsFromExternalSystem(final ApiKeyBaseEvent event) {
    log.entry(event);
    
    boolean result;
    if (system.equals(event.getApplication())) {
      log.debug("System is the same. Ignoring event. event={}", event);
      result = false;
    } else {
      result = true;
    }
    
    return log.exit(result);
  }
}
