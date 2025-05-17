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


import de.kaiserpfalzedv.commons.users.domain.services.ApiKeyReadService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class JpaApiKeyReadService implements ApiKeyReadService {
  private final ApiKeyRepository repository;
  
  
  @Override
  public Optional<ApiKeyJPA> retrieve(final UUID id) {
    log.entry(id);
    
    return log.exit(repository.findById(id));
  }
  
  @Override
  public Optional<ApiKeyJPA> retrieve(final String id) {
    log.entry(id);
    
    return log.exit(repository.findById(UUID.fromString(id)));
  }
  
  @Override
  public List<ApiKeyJPA> retrieveForUser(final UUID userId) {
    log.entry(userId);
    
    return log.exit(repository.findByUserId(userId));
  }
}
