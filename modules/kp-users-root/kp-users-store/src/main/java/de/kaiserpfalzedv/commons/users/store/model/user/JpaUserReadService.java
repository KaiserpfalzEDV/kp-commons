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

package de.kaiserpfalzedv.commons.users.store.model.user;

import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Order(1010)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class JpaUserReadService implements UserReadService<UserJPA> {
    private final UserRepository users;
    
    @Override
    @Counted
    @Timed
    public Optional<UserJPA> findById(final UUID id) {
        log.entry(id);
        
        return log.exit(users.findById(id));
    }
    
    @Override
    @Counted
    @Timed
    public List<UserJPA> findByNamespace(final String nameSpace) {
        log.entry(nameSpace);
        
        return log.exit(users.findByNameSpace(nameSpace));
    }
    
    @Override
    @Counted
    @Timed
    public Page<UserJPA> findByNamespace(final String nameSpace, final Pageable pageable) {
        log.entry(nameSpace, pageable);
        
        return log.exit(users.findByNameSpace(nameSpace, pageable));
    }
    
    @Override
    @Counted
    @Timed
    public List<UserJPA> findAll() {
        log.entry();
        
        return log.exit(users.findAll());
    }
    
    @Override
    @Counted
    @Timed
    public Page<UserJPA> findAll(final Pageable pageable) {
        log.entry(pageable);
        
        return log.exit(users.findAll(pageable));
    }
    
    @Override
    @Counted
    @Timed
    public Optional<UserJPA> findByUsername(final String namespace, final String name) {
        log.entry(namespace, name);
        
        return log.exit(users.findByNameSpaceAndName(namespace, name));
    }
    
    @Override
    @Counted
    @Timed
    public Optional<UserJPA> findByOauth(final String issuer, final String sub) {
        log.entry(issuer, sub);
        
        return log.exit(users.findByIssuerAndSubject(issuer, sub));
    }
}
