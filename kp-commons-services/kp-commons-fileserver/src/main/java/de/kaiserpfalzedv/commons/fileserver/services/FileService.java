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

package de.kaiserpfalzedv.commons.fileserver.services;

import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFile;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileData;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileRepository;
import io.quarkus.panache.common.Sort;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FileService -- Save a file or retrieve it.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0  2021-12-31
 * @since 2.0.0  2021-12-31
 */
@Slf4j
@ApplicationScoped
public class FileService {
    @Inject
    JPAFileRepository repository;

    @PostConstruct
    public void init() {
        log.info("Started file service. repository={}", repository);
    }

    @PreDestroy
    public void close() {
        log.info("Closing file service ...");
    }

    public List<File> index(
            final String nameSpace,
            final String mediaType,
            final String owner,
            final int size,
            final int page,
            List<String> sort,
            final Principal principal,
            final Set<String> roles
            ) {
        log.info(
                "List files. namespace='{}', mediaType='{}', size={}, page={}, sort[]={}",
                nameSpace, mediaType, size, page, sort
        );

        String owned = principal.getName();
        Sort order = calculateSort(sort);

        Stream<JPAFile> data = repository.streamAll(order);

        // only display files owned by 'owner'
        if (owner != null) {
            data = data.filter(d -> d.getOwner().equalsIgnoreCase(owned));
        }

        // filter all files the user has no access to.
        data = data.filter(d -> d.to().hasAccess(owned, roles, File.READ));

        return data.map(JPAFile::to).collect(Collectors.toList());
    }


    Sort calculateSort(@NotNull List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            log.trace("No columns to sort by given. Setting default column order (namespace, owner, name)");

            columns = List.of("nameSpace", "owner", "file.name");
        }

        log.debug("Setting sort order. columns={}", columns);
        return Sort.ascending(columns.toArray(new String[0]));

    }


    @Transactional
    public File create(
            @Size(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH,
                    message = HasName.VALID_NAME_LENGTH_MSG)
            @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
            @NotNull final File input
    ) {
        log.info("Creating file. namespace='{}', name='{}'",
                input.getNameSpace(), input.getName()
        );

        if (input.getUid() != null) {
            log.warn("Removing UUID to create a new entity.");
        }

        JPAFile stored = JPAFile.from(input)
                .toBuilder()
                .id(null)
                .build();

        persist(stored);

        log.info("Created entity. uuid='{}', namespace='{}', name='{}'",
                stored.getId(), stored.getNameSpace(), stored.getName());
        return stored.to();
    }

    private void persist(JPAFile store) {
        log.debug("Trying to persist entity ...");

        repository.persistAndFlush(store);

        log.debug("Persisted entity. uuid='{}', namespace='{}', name='{}'",
                store.getId(), store.getNameSpace(), store.getName());
    }


    @Transactional
    public File update(
            @NotNull final File input,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles
    ) {
        log.info("Updating file. uuid='{}', namespace='{}', name='{}'",
                input.getUid(), input.getNameSpace(), input.getName()
        );

        JPAFile stored = repository.findById(input.getUid());
        if (stored == null) {
            log.warn("Entity with UID does not exist. Creating a new one.");
            return create(input);
        }

        checkPermission(stored, File.WRITE, principal, roles);

        log.trace("Updating the data of entity.");
        Metadata metadata = input.getMetadata();

        updateGroup(stored, metadata);
        updateOwner(stored, metadata, principal);
        updatePermissions(stored, metadata);

        stored.setFile(JPAFileData.from(input.getSpec().getFile()));

        if (input.getSpec().getPreview() != null) {
            stored.setPreview(JPAFileData.from(input.getSpec().getPreview()));
        }

        persist(stored);

        log.info("Updated entity. uuid='{}', namespace='{}', name='{}'",
                stored.getId(), stored.getNameSpace(), stored.getName());
        return stored.to();
    }
    
    private void checkPermission(final JPAFile stored, final int permission, final Principal principal, Set<String> roles) {
        if (!stored.to().hasAccess(principal.getName(), roles, permission)) {
            throw new SecurityException();
        }
    }

    private void updateGroup(final JPAFile stored, final Metadata metadata) {
        metadata.getOwningResource().ifPresent(
                o -> stored.setGroup(o.getNameSpace())
        );

        metadata.getAnnotation(File.ANNOTATION_GROUP).ifPresent(stored::setGroup);
    }

    private void updateOwner(final JPAFile stored, final Metadata metadata, final Principal principal) {
        if (stored.getOwner().equals(principal.getName())) {
            String oldOwner = stored.getOwner();

            metadata.getOwningResource().ifPresent(
                    o -> stored.setOwner(o.getName())
            );

            metadata.getAnnotation(File.ANNOTATION_OWNER).ifPresent(stored::setOwner);

            if (!oldOwner.equals(stored.getOwner())) {
                log.info("Handed over ownership of file. id={}, nameSpace='{}', name='{}', owner.old='{}', owner.new='{}",
                        stored.getId(), stored.getNameSpace(), stored.getName(), oldOwner, stored.getOwner());
            }
        }
    }

    private void updatePermissions(final JPAFile stored, final Metadata metadata) {
        metadata.getAnnotation(File.ANNOTATION_PERMISSIONS).ifPresent(stored::setPermissions);
    }


    public File resource(
            @NotNull final UUID id,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles
    ) {
        log.info("Retrieving file. uuid='{}'", id);

        JPAFile data = repository.findById(id);

        if (data == null) {
            throw new NotFoundException("No file with ID '" + id + "' found.");
        }

        checkPermission(data, File.READ, principal, roles);
        return data.to();
    }


    public File resource(
            @NotNull final String nameSpace,
            @NotNull final String name,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles

    ) {
        log.info("Retrieving file. namespace='{}', name='{}'",
                nameSpace, name
        );
        Optional<JPAFile> data = repository.findByNameSpaceAndName(nameSpace, name);

        if (data.isEmpty()) {
            throw new NotFoundException(String.format(
                    "No file with name space '%s' and name '%s' found.",
                    nameSpace, name
            ));
        }

        checkPermission(data.get(), File.READ, principal, roles);

        return data.get().to();
    }


    @Transactional
    public void delete(
            @NotNull final UUID id,
            @NotNull final Principal principal,
            @NotNull final Set<String> roles
    ) {
        log.info("Deleting file. uuid='{}'", id);

        Optional<JPAFile> orig = repository.findByIdOptional(id);
        orig.ifPresentOrElse(
                o -> {
                    if (o.to().hasAccess(principal.getName(), roles, File.WRITE)) {
                        remove(o);
                        log.info("Deleted file. id={}, nameSpace='{}', name='{}'", id, o.getNameSpace(), o.getName());
                    } else {
                        log.warn("User has no write access to the file. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                                principal.getName(),roles,
                                id, o.getNameSpace(), o.getName());
                    }
                },
                () -> log.info("No file with ID found. Everything is fine, it should have been deleted nevertheless. id={}",
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
        log.info("Deleting file. namespace='{}', name='{}'",
                nameSpace, name
        );
        Optional<JPAFile> orig = repository.findByNameSpaceAndName(nameSpace, name);

        orig.ifPresentOrElse(
                o -> {
                    if (o.to().hasAccess(principal.getName(), roles, File.WRITE)) {
                        remove(o);
                        log.info("Deleted file. id={}, nameSpace='{}', name='{}'", o.getId(), o.getNameSpace(), o.getName());
                    } else {
                        log.warn("User has no write access to the file. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                                principal.getName(), roles,
                                o.getId(), o.getNameSpace(), o.getName());
                    }
                },
                () -> log.info("No file with nameSpace and name found. Everything is fine, it should have been deleted nevertheless. nameSpace='{}', name='{}'",
                        nameSpace, name)
        );
    }

    private void remove(final JPAFile input) {
        log.debug("Trying to delete entity ...");
        repository.deleteById(input.getId());

        log.info("Deleted file. id={}, nameSpace='{}', name='{}'", input.getId(), input.getNameSpace(), input.getName());
    }
}
