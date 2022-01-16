/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.fileserver.services;

import de.kaiserpfalzedv.commons.core.api.About;
import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFile;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileRepository;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
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
import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
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
    public File create(
            @QueryParam("nameSpace") final String nameSpace,
            @NotNull final File input
    ) {
        JPAFile store = JPAFile.from(input);

        repository.persistAndFlush(store);

        return resource(store.getId());
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
            @Length(
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
            @Length(
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
        Optional<JPAFile> data = repository.findByNameSpaceAndName(nameSpace, name);

        if (data.isEmpty()) {
            throw new NotFoundException(String.format(
                    "No file with name space '%s' and name '%s' found.",
                    nameSpace, name
            ));
        }

        return data.get().to();
    }
}
