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


import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyCreatedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyRevokedEvent;
import de.kaiserpfalzedv.commons.users.domain.services.ApiKeysEventsHandler;
import de.kaiserpfalzedv.commons.users.store.model.apikey.ApiKeyJPA;
import de.kaiserpfalzedv.commons.users.store.model.apikey.ApiKeyRepository;
import de.kaiserpfalzedv.commons.users.store.model.apikey.ApiKeyToJPA;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
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
public class JpaApiKeyEventsHandler implements ApiKeysEventsHandler {
  private final ApiKeyRepository repository;
  private final ApiKeyToJPA toJPA;
  
  @Override
  public void event(final ApiKeyCreatedEvent event) {
    log.entry(event);
    
    ApiKeyJPA result = repository.save(toJPA.apply(event.getApiKey()));
    log.info("created api key: key='***', user='{}'", result.getUser());
    
    log.exit(result);
  }
  
  @Override
  public void event(final ApiKeyRevokedEvent event) {
    log.entry(event);
    
    repository.deleteById(event.getApiKey().getId());
    log.info("Revoked api key: key='{}', user='{}'", event.getApiKey().getName(), event.getUser());
    
    log.exit(event.getApiKey());
  }
}
