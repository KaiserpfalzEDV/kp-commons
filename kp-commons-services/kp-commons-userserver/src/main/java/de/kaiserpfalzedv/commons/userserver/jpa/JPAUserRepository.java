/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.userserver.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * JPAUserRepository -- A generic repository to handle users.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.3.0  2022-05-30
 * @since 2.3.0  2022-05-30
 */
@ApplicationScoped
public class JPAUserRepository implements PanacheRepositoryBase<JPAUser, UUID> {
    /**
     * Retrieves the file by namespace and name.
     *
     * @param nameSpace NameSpace of the file to retrieve.
     * @param name      Name of the file to retrieve.
     * @return The file retrieved.
     */
    public Optional<JPAUser> findByNameSpaceAndName(
            @NotNull final String nameSpace,
            @NotNull final String name
    ) {
        return find("nameSpace = ?1 and name = ?2", nameSpace, name)
                .firstResultOptional();
    }
}