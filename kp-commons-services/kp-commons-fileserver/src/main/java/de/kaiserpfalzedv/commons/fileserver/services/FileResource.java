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

import de.kaiserpfalzedv.commons.core.api.About;
import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.rest.HttpErrorGenerator;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFile;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileData;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileRepository;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PessimisticLockException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@Path("/api/file/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
        info = @Info(
                title = "KP FileService",
                version = "2.0.0",
                description = "This is a generic fileservice for storing files",
                contact = @Contact(
                        name = "Kaiserpfalz EDV-Service Tech Support",
                        email = "tech@kaiserpfalz-edv.de",
                        url = "https://www.kaiserpfalz-edv.de"
                ),
                license = @License(
                        name = "GPL v3 or newer",
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                ),
                termsOfService = "https://www.kaiserpfalz-edv.de/impressum"
        )
)
public class FileResource {
    @Inject
    JPAFileRepository repository;

    @Inject
    HttpErrorGenerator errorGenerator;

    @Inject
    SecurityIdentity identity;

    @PostConstruct
    public void init() {
        log.info("Started file service. repository={}", repository);
    }

    @PreDestroy
    public void close() {
        log.info("Closing file service ...");
    }

    @Schema(
            description = "Index of all files."
    )
    @GET
    @Path("/")
    @RolesAllowed({"user", "admin"})
    @NoCache
    @Operation(
            summary = "List all files available.",
            description = "Returns a list of files."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Ok found."),
            @APIResponse(responseCode = "403", description = "Forbidden."),
            @APIResponse(responseCode = "404", description = "Not found.")
    })
    @SecurityRequirements({
            @SecurityRequirement(name = "basic", scopes = {"user", "admin"})
    })
    public List<File> index(
            @Schema(
                    description = "Namespace to search for.",
                    required = true,
                    defaultValue = About.NAMESPACE,
                    name = "nameSpace",
                    example = About.NAMESPACE,
                    maxLength = 50
            )
            @QueryParam("namespace") final String nameSpace,
            @Schema(
                    description = "MediaType to look for.",
                    nullable = true,
                    name = "mediaType",
                    example = "image/png",
                    maxLength = 100
            )
            @QueryParam("mediaType") final String mediaType,
            @Schema(
                    description = "The owner of the file.",
                    nullable = true,
                    name = "owner",
                    example = "klenkes74",
                    maxLength = 100
            )
            @QueryParam("owner") final String owner,
            @Schema(
                    description = "Size of the page to return",
                    nullable = true,
                    example = "100",
                    defaultValue = "100"
            )
            @QueryParam("size") final int size,
            @Schema(
                    description = "Which page to return (starting with page 1)",
                    nullable = true,
                    example = "1",
                    defaultValue = "1"
            )
            @QueryParam("page") final int page,
            @Schema(
                    description = "The data sort rules.",
                    nullable = true,
                    minItems = 0,
                    maxItems = 5,
                    implementation = List.class,
                    defaultValue = "[\"nameSpace\",\"owner\",\"group\",\"name\"]",
                    enumeration = {"nameSpace", "owner", "file.mimeType", "group", "name"}
            )
            @QueryParam("sort") List<String> sort
    ) {
        log.info(
                "List files. namespace='{}', mediaType='{}', size={}, page={}, sort[]={}",
                nameSpace, mediaType, size, page, sort
        );

        String owned = identity.getPrincipal().getName();
        Sort order = calculateSort(sort);

        Stream<JPAFile> data = repository.streamAll(order);

        if (owner != null) {
            data = data.filter(d -> d.getOwner().equalsIgnoreCase(owned));
        }

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


    @NoCache
    @POST
    @RolesAllowed({"user", "admin"})
    @Operation(
            summary = "Creates a new file.",
            description = "Creates a new file with the given data."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "409",
                    name = "Conflict",
                    description = "The Resource already exists (UUID or NameSpace and Name)."
            )
    })
    @Transactional
    public File create(
            @Schema(
                    description = "The file to be saved with all metadata needed",
                    required = true,
                    example = HasName.VALID_NAME_EXAMPLE,
                    pattern = HasName.VALID_NAME_PATTERN,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH
            )
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

        JPAFile store = JPAFile.from(input)
                .toBuilder()
                .id(null)
                .build();

        persist(input, store);

        log.info("Created entity. uuid='{}', namespace='{}', name='{}'",
                store.getId(), store.getNameSpace(), store.getName());
        return resource(store.getId());
    }

    private void persist(File input, JPAFile store) {
        log.debug("Trying to persist entity ...");
        try {
            repository.persistAndFlush(store);

            log.debug("Persisted entity. uuid='{}', namespace='{}', name='{}'",
                    store.getId(), store.getNameSpace(), store.getName());
        } catch (javax.persistence.EntityExistsException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.CONFLICT,
                    "Either UUID or NameSpace+Name already taken",
                    Map.of(
                            "UUID", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (PessimisticLockException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "UUID", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (RuntimeException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "UUID", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        }
    }


    @NoCache
    @PUT
    @RolesAllowed({"user", "admin"})
    @Operation(
            summary = "Updates the file",
            description = "This resource updates the file. If the file does not exist, it get's created."
    )
    @APIResponses({
            @APIResponse(responseCode = "401", name = "Forbidden", description = "You are not allowed to change this file"),
            @APIResponse(
                    responseCode = "409",
                    name = "Conflict",
                    description = "The Resource already exists (UUID or NameSpace and Name)."
            )
    })
    @Transactional
    public File update(
            @Schema(
                    description = "The file data to be stored.",
                    required = true
            )
            @NotNull final File input
    ) {
        log.info("Updating file. uuid='{}', namespace='{}', name='{}'",
                input.getUid(), input.getNameSpace(), input.getName()
        );

        JPAFile stored = repository.findById(input.getUid());
        if (stored == null) {
            log.warn("Entity with UID does not exist. Creating a new one.");
            return create(input);
        }

        checkPermission(input, stored, File.WRITE);

        log.trace("Updating the data of entity.");
        Metadata metadata = input.getMetadata();

        updateGroup(stored, metadata);
        updateOwner(stored, metadata);
        updatePermissions(stored, metadata);

        stored.setFile(JPAFileData.from(input.getSpec().getFile()));

        if (input.getSpec().getPreview() != null) {
            stored.setPreview(JPAFileData.from(input.getSpec().getPreview()));
        }

        persist(input, stored);

        log.info("Updated entity. uuid='{}', namespace='{}', name='{}'",
                stored.getId(), stored.getNameSpace(), stored.getName());
        return resource(stored.getId());
    }
    
    private void checkPermission(final File input, final JPAFile stored, final int permission) {
        if (!stored.to().hasAccess(identity.getPrincipal().getName(), identity.getRoles(), permission)) {
            errorGenerator.throwHttpProblem(
                    Response.Status.FORBIDDEN,
                    "User has no write access to this file!",
                    Map.of(
                            "user", identity.getPrincipal().getName(),
                            "groups", identity.getRoles().stream()
                                    .collect(Collectors.joining(",", "{", "}")),
                            "owner", stored.getOwner(),
                            "group", stored.getGroup(),
                            "permission", stored.getPermissions(),
                            "UUID", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        }
    }

    private void updateGroup(final JPAFile stored, final Metadata metadata) {
        metadata.getOwningResource().ifPresent(
                o -> stored.setGroup(o.getNameSpace())
        );

        metadata.getAnnotation(File.ANNOTATION_GROUP).ifPresent(stored::setGroup);
    }

    private void updateOwner(final JPAFile stored, final Metadata metadata) {
        if (stored.getOwner().equals(identity.getPrincipal().getName())) {
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


    @Path("/{id}")
    @GET
    @RolesAllowed({"user", "admin"})
    @Operation(
            summary = "Retrieves the file with the given UID",
            description = "Retrieves the file by the unique ID of every file.",
            hidden = true
    )
    public File resource(
            @Schema(
                    description = "The ID of the file.",
                    required = true,
                    minLength = 36,
                    maxLength = 36,
                    example = "628a8172-2d9f-4f26-bd64-fac95ce7adc2"
            )
            @PathParam("id") @NotNull final UUID id
    ) {
        log.info("Retrieving file. uuid='{}'", id);

        JPAFile data = repository.findById(id);

        if (data == null) {
            throw new NotFoundException("No file with ID '" + id + "' found.");
        }

        return data.to();
    }


    @GET
    @Path("/{nameSpace}/{name}")
    @Operation(
            summary = "Retrieves the file",
            description = "Retrieves the file by Name Space and Name"
    )
    public File resource(
            @Schema(
                    description = "The name space of the file.",
                    required = true,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH,
                    pattern = HasName.VALID_NAME_PATTERN,
                    example = "default"
            )
            @Size(
                    min = HasName.VALID_NAME_MIN_LENGTH,
                    max = HasName.VALID_NAME_MAX_LENGTH,
                    message = "Invalid length. Must be between "
                            + HasName.VALID_NAME_MIN_LENGTH
                            + " and "
                            + HasName.VALID_NAME_MAX_LENGTH
                            + " characters in size."
            )
            @Pattern(
                    regexp = HasName.VALID_NAME_PATTERN,
                    message = "Invalid content. Must follow the rules for domain names (pattern: '"
                            + HasName.VALID_NAME_PATTERN + "')."
            )
            @PathParam("nameSpace")
            @NotNull final String nameSpace,
            @Schema(
                    description = "The name of the file.",
                    required = true,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH,
                    pattern = HasName.VALID_NAME_PATTERN,
                    example = "default"
            )
            @Size(
                    min = HasName.VALID_NAME_MIN_LENGTH,
                    max = HasName.VALID_NAME_MAX_LENGTH,
                    message = "Invalid length. Must be between "
                            + HasName.VALID_NAME_MIN_LENGTH
                            + " and "
                            + HasName.VALID_NAME_MAX_LENGTH
                            + " characters in size."
            )
            @Pattern(
                    regexp = HasName.VALID_NAME_PATTERN,
                    message = "Invalid content. Must follow the rules for domain names (pattern: '"
                            + HasName.VALID_NAME_PATTERN + "')."
            )
            @PathParam("name")
            @NotNull final String name
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

        return data.get().to();
    }


    @Transactional
    @NoCache
    @DELETE
    @RolesAllowed({"user", "admin"})
    @Path("/{uid}")
    @Operation(
            summary = "Deletes the file by UUID",
            description = "Deletes the file with the given UUID as long as the user is allowed to write that file"
    )
    public void delete(
            @Schema(
                    description = "UUID of the file to be deleted."
            )
            @PathParam("uid") final UUID id
    ) {
        log.info("Deleting file. uuid='{}'", id);

        Optional<JPAFile> orig = repository.findByIdOptional(id);
        orig.ifPresentOrElse(
                o -> {
                    if (o.to().hasAccess(identity.getPrincipal().getName(), identity.getRoles(), File.WRITE)) {
                        remove(o);
                        log.info("Deleted file. id={}, nameSpace='{}', name='{}'", id, o.getNameSpace(), o.getName());
                    } else {
                        log.warn("User has no write access to the file. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                                identity.getPrincipal().getName(), identity.getRoles(),
                                id, o.getNameSpace(), o.getName());
                    }
                },
                () -> log.info("No file with ID found. Everything is fine, it should have been deleted nevertheless. id={}",
                        id)
        );
    }

    @Transactional
    @NoCache
    @DELETE
    @RolesAllowed({"user", "admin"})
    @Path("/{nameSpace}/{name}")
    @Operation(
            summary = "Deletes the file by UUID",
            description = "Deletes the file with the given UUID as long as the user is allowed to write that file"
    )
    public void delete(

            @Schema(
                    description = "The name space of the file.",
                    required = true,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH,
                    pattern = HasName.VALID_NAME_PATTERN,
                    example = "default"
            )
            @Size(
                    min = HasName.VALID_NAME_MIN_LENGTH,
                    max = HasName.VALID_NAME_MAX_LENGTH,
                    message = "Invalid length. Must be between "
                            + HasName.VALID_NAME_MIN_LENGTH
                            + " and "
                            + HasName.VALID_NAME_MAX_LENGTH
                            + " characters in size."
            )
            @Pattern(
                    regexp = HasName.VALID_NAME_PATTERN,
                    message = "Invalid content. Must follow the rules for domain names (pattern: '"
                            + HasName.VALID_NAME_PATTERN + "')."
            )
            @PathParam("nameSpace")
            @NotNull final String nameSpace,
            @Schema(
                    description = "The name of the file.",
                    required = true,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH,
                    pattern = HasName.VALID_NAME_PATTERN,
                    example = "default"
            )
            @Size(
                    min = HasName.VALID_NAME_MIN_LENGTH,
                    max = HasName.VALID_NAME_MAX_LENGTH,
                    message = "Invalid length. Must be between "
                            + HasName.VALID_NAME_MIN_LENGTH
                            + " and "
                            + HasName.VALID_NAME_MAX_LENGTH
                            + " characters in size."
            )
            @Pattern(
                    regexp = HasName.VALID_NAME_PATTERN,
                    message = "Invalid content. Must follow the rules for domain names (pattern: '"
                            + HasName.VALID_NAME_PATTERN + "')."
            )
            @PathParam("name")
            @NotNull final String name
    ) {
        log.info("Deleting file. namespace='{}', name='{}'",
                nameSpace, name
        );
        Optional<JPAFile> orig = repository.findByNameSpaceAndName(nameSpace, name);

        orig.ifPresentOrElse(
                o -> {
                    if (o.to().hasAccess(identity.getPrincipal().getName(), identity.getRoles(), File.WRITE)) {
                        remove(o);
                        log.info("Deleted file. id={}, nameSpace='{}', name='{}'", o.getId(), o.getNameSpace(), o.getName());
                    } else {
                        log.warn("User has no write access to the file. user='{}', groups={}, id={}, nameSpace='{}', name='{}'",
                                identity.getPrincipal().getName(), identity.getRoles(),
                                o.getId(), o.getNameSpace(), o.getName());
                    }
                },
                () -> log.info("No file with nameSpace and name found. Everything is fine, it should have been deleted nevertheless. nameSpace='{}', name='{}'",
                        nameSpace, name)
        );
    }

    private void remove(final JPAFile input) {
        log.debug("Trying to delete entity ...");
        try {
            repository.deleteById(input.getId());
        } catch (javax.persistence.EntityExistsException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.CONFLICT,
                    "Either UUID or NameSpace+Name already taken",
                    Map.of(
                            "UUID", input.getId().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (PessimisticLockException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "UUID", input.getId().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (RuntimeException cause) {
            errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "UUID", input.getId().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        }
    }
}
