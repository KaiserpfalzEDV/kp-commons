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
import de.kaiserpfalzedv.commons.core.rest.HttpErrorGenerator;
import io.quarkus.security.identity.SecurityIdentity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PessimisticLockException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    FileService service;

    @Inject
    HttpErrorGenerator errorGenerator;

    @Inject
    SecurityIdentity identity;

    @PostConstruct
    public void init() {
        log.info("Started file rest resource. service={}, errorGenerator={}", service, errorGenerator);
    }

    @PreDestroy
    public void close() {
        log.info("Closing file rest resource ...");
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
        return service.index(nameSpace, mediaType, owner, size, page, sort, identity.getPrincipal(), identity.getRoles());
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
    public File create(
            @Schema(
                    description = "The file to be saved with all metadata needed",
                    required = true,
                    example = HasName.VALID_NAME_EXAMPLE,
                    pattern = HasName.VALID_NAME_PATTERN,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH
            )
            @NotNull final File input
    ) {
        try {
            return service.create(input);
        } catch (javax.persistence.EntityExistsException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.CONFLICT,
                    "Either UUID or NameSpace+Name already taken",
                    Map.of(
                            "uuid", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (PessimisticLockException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "uuid", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (RuntimeException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "uuid", input.getUid().toString(),
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
    public File update(
            @Schema(
                    description = "The file data to be stored.",
                    required = true
            )
            @NotNull final File input
    ) {
        try {
            return service.update(input, identity.getPrincipal(), identity.getRoles());
        } catch (SecurityException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.FORBIDDEN,
                    "User has no write access to this file!",
                    Map.of(
                            "user", identity.getPrincipal().getName(),
                            "groups",identity.getRoles().stream()
                                    .collect(Collectors.joining(",", "{", "}")),
                            "UUID", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (javax.persistence.EntityExistsException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.CONFLICT,
                    "Either UUID or NameSpace+Name already taken",
                    Map.of(
                            "uuid", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (PessimisticLockException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "uuid", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        } catch (RuntimeException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "uuid", input.getUid().toString(),
                            "NameSpace", input.getNameSpace(),
                            "Name", input.getName()
                    )
            );
        }
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
        return service.resource(id, identity.getPrincipal(), identity.getRoles());
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
        return service.resource(nameSpace, name, identity.getPrincipal(), identity.getRoles());
    }


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
        try {
        service.delete(id, identity.getPrincipal(), identity.getRoles());
        } catch (PessimisticLockException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "uuid", id.toString()
                    )
            );
        } catch (RuntimeException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "uuid", id.toString()
                    )
            );
        }

    }

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
        try {
            service.delete(nameSpace, name, identity.getPrincipal(), identity.getRoles());
        } catch (PessimisticLockException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The data set has been changed by another transaction",
                    Map.of(
                            "NameSpace", nameSpace,
                            "Name", name
                    )
            );
        } catch (RuntimeException cause) {
            throw errorGenerator.throwHttpProblem(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    cause.getMessage(),
                    Map.of(
                            "NameSpace", nameSpace,
                            "Name", name
                    )
            );
        }
    }
}
