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

import de.kaiserpfalzedv.commons.users.store.model.role.RoleJPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-08-18
 */
@Repository
public interface UserRepository extends JpaRepository<UserJPA, UUID> {
    Optional<UserJPA> findByNameSpaceAndName(String nameSpace, String name);
    Optional<UserJPA> findByIssuerAndSubject(String issuer, String subject);
    Optional<UserJPA> findByEmail(String email);

    List<UserJPA> findByNameSpace(String nameSpace);
    Page<UserJPA> findByNameSpace(String nameSpace, Pageable pageable);
    
    List<UserJPA> findByIssuer(String issuer);
    Page<UserJPA> findByIssuer(String issuer, Pageable pageable);
    
    List<UserJPA> findByAuthoritiesContains(Set<RoleJPA> authorities);
    
}
