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

package de.kaiserpfalzedv.commons.userserver.services;

import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.user.User;
import de.kaiserpfalzedv.commons.core.user.UserData;
import de.kaiserpfalzedv.commons.userserver.jpa.JPAUser;
import de.kaiserpfalzedv.commons.userserver.jpa.JPAUserData;
import de.kaiserpfalzedv.commons.userserver.jpa.JPAUserRepository;
import io.quarkus.panache.common.Sort;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UserService -- Save a user or retrieve it.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.3.0  2022-05-30
 * @since 2.3.0  2022-05-30
 */
@Slf4j
@ApplicationScoped
public class UserService {
    @Inject
    JPAUserRepository repository;

    @PostConstruct
    public void init() {
        log.info("Started user service. repository={}", repository);
    }

    @PreDestroy
    public void close() {
        log.info("Closing user service ...");
    }

    public List<User> index(
            final String nameSpace,
            final int size,
            final int page,
            List<String> sort
    ) {
        log.info(
                "List users. namespace='{}', size={}, page={}, sort[]={}",
                nameSpace, size, page, sort
        );

        Sort order = calculateSort(sort);

        Stream<JPAUser> data = repository.streamAll(order);

        return data.map(JPAUser::to).collect(Collectors.toList());
    }


    Sort calculateSort(@NotNull List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            log.trace("No columns to sort by given. Setting default column order (namespace, owner, name)");

            columns = List.of("nameSpace", "user.name");
        }

        log.debug("Setting sort order. columns={}", columns);
        return Sort.ascending(columns.toArray(new String[0]));

    }


    @Transactional
    public User create(
            @NotNull final User input
    ) {
        log.info("Creating user. namespace='{}', name='{}'",
                input.getNameSpace(), input.getName()
        );

        if (input.getUid() != null) {
            log.warn("Removing UUID to create a new entity.");
        }

        JPAUser stored = JPAUser.from(input)
                .toBuilder()
                .id(null)
                .build();

        persist(stored);

        log.info("Created entity. uuid='{}', namespace='{}', name='{}'",
                stored.getId(), stored.getNameSpace(), stored.getName());
        return stored.to();
    }

    private void persist(JPAUser store) {
        log.debug("Trying to persist entity ...");

        repository.persistAndFlush(store);

        log.debug("Persisted entity. uuid='{}', namespace='{}', name='{}'",
                store.getId(), store.getNameSpace(), store.getName());
    }


    @Transactional
    public User update(
            @NotNull final User input
    ) {
        log.info("Updating user. uuid='{}', namespace='{}', name='{}'",
                input.getUid(), input.getNameSpace(), input.getName()
        );

        JPAUser stored = repository.findById(input.getUid());
        if (stored == null) {
            log.warn("Entity with UID does not exist. Creating a new one.");
            return create(input);
        }

        log.trace("Updating the data of entity.");
        Metadata metadata = input.getMetadata();

        stored.setIssuer(metadata.getLabel(UserData.ISSUER).orElseThrow());
        stored.setSubject(metadata.getLabel(UserData.SUBJECT).orElseThrow());

        stored.setUser(JPAUserData.from(input.getSpec()));

        persist(stored);

        log.info("Updated entity. uuid='{}', namespace='{}', name='{}'",
                stored.getId(), stored.getNameSpace(), stored.getName());
        return stored.to();
    }

    public User resource(
            @NotNull final UUID id
    ) {
        log.info("Retrieving user. uuid='{}'", id);

        JPAUser data = repository.findById(id);

        if (data == null) {
            throw new NotFoundException("No user with ID '" + id + "' found.");
        }

        return data.to();
    }


    public User resource(
            @NotNull final String nameSpace,
            @NotNull final String name

    ) {
        log.info("Retrieving user. namespace='{}', name='{}'",
                nameSpace, name
        );
        Optional<JPAUser> data = repository.findByNameSpaceAndName(nameSpace, name);

        if (data.isEmpty()) {
            throw new NotFoundException(String.format(
                    "No user with name space '%s' and name '%s' found.",
                    nameSpace, name
            ));
        }

        return data.get().to();
    }


    @Transactional
    public void delete(
            @NotNull final UUID id,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles
    ) {
        log.info("Deleting user. uuid='{}'", id);

        Optional<JPAUser> orig = repository.findByIdOptional(id);
        orig.ifPresentOrElse(
                o -> {
                    log.warn("User has no write access to the user. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                            principal.getName(), roles,
                            id, o.getNameSpace(), o.getName());
                },
                () -> log.info("No user with ID found. Everything is fine, it should have been deleted nevertheless. id={}",
                        id)
        );
    }

    @Transactional
    public void delete(
            @NotNull final String nameSpace,
            @NotNull final String name,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles
    ) {
        log.info("Deleting user. namespace='{}', name='{}'",
                nameSpace, name
        );
        Optional<JPAUser> orig = repository.findByNameSpaceAndName(nameSpace, name);

        orig.ifPresentOrElse(
                o -> {
                    log.warn("User has no write access to the user. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                            principal.getName(), roles,
                            o.getId(), o.getNameSpace(), o.getName());
                },
                () -> log.info("No user with nameSpace and name found. Everything is fine, it should have been deleted nevertheless. nameSpace='{}', name='{}'",
                        nameSpace, name)
        );
    }

    private void remove(final JPAUser input) {
        log.debug("Trying to delete entity ...");
        repository.deleteById(input.getId());

        log.info("Deleted user. id={}, nameSpace='{}', name='{}'", input.getId(), input.getNameSpace(), input.getName());
    }
}
