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

package de.kaiserpfalzedv.commons.users.store.model.apikey;


import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKey;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKeyNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.services.ApiKeyWriteService;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaApiKeyWriteService implements ApiKeyWriteService {
  private final ApiKeyRepository repository;
  private final ApiKeyToJPAImpl toJpa;
  
  @Override
  public void create(final ApiKey apiKey) throws InvalidApiKeyException {
    log.entry(apiKey);
    
    ApiKeyJPA data = (ApiKeyJPA.class.isAssignableFrom(apiKey.getClass()))
                     ? (ApiKeyJPA) apiKey
                     : toJpa.apply(apiKey);
    
    try {
      repository.save(data);
    } catch (final IllegalArgumentException | OptimisticLockingFailureException e) {
      throw log.throwing(new InvalidApiKeyException(apiKey, e));
    }
    
    log.exit();
  }
  
  @Override
  public ApiKeyJPA refresh(final UUID apiKeyId, final long days) throws ApiKeyNotFoundException {
    log.entry(apiKeyId, days);
    
    Optional<ApiKeyJPA> data = repository.findById(apiKeyId);
    if (data.isEmpty()) {
      throw log.throwing(new ApiKeyNotFoundException(apiKeyId));
    }
    ApiKeyJPA result = data.get().toBuilder().expiration(OffsetDateTime.now().plusDays(days)).build();
    repository.save(result);
    
    return log.exit(result);
  }
  
  @Override
  public void delete(final UUID apiKeyId) {
    log.entry(apiKeyId);
    
    repository.deleteById(apiKeyId);
    
    log.exit();
  }
}
