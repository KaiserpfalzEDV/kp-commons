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

import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFile;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileData;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * DeliverResource -- Save a file or retrieve it.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.1.0  2022-01-16
 * @since 2.1.0  2022-01-16
 */
@Slf4j
@ApplicationScoped
@Path("/public/deliver")
@PermitAll
public class DeliverResource {
    @Inject
    JPAFileRepository repository;

    @Path("/{type}/{id}")
    @GET
    @Operation(
            summary = "Retrieves the file with the given UID",
            description = "Retrieves the file by the unique ID of every file.",
            hidden = true
    )
    public Response resource(
            @Schema(
                    description = "The requested file type (file or preview)",
                    required = true,
                    enumeration = {"file", "preview"},
                    example = "file"
            )
            @PathParam("type") @NotNull final String type,
            @Schema(
                    description = "The ID of the file.",
                    required = true,
                    minLength = HasId.VALID_UUID_LENGTH,
                    maxLength = HasId.VALID_UUID_LENGTH,
                    pattern = HasId.VALID_UUID_PATTERN,
                    example = HasId.VALID_UUID_EXAMPLE
            )
            @PathParam("id") @NotNull final UUID id
    ) {
        Optional<JPAFile> data = repository.findByIdOptional(id);

        checkDataFound(data.isEmpty(), String.format("No file with id '%s' found.", id));

        Optional<JPAFileData> file = retrieveFileOrPreview(data.get(), type);
        return returnFile(data.get(), file);
    }

    private Optional<JPAFileData> retrieveFileOrPreview(JPAFile data, final String type) {
        switch (type) {
            case "file":
                return Optional.of(data.getFile());
            case "preview":
                return Optional.ofNullable(data.getPreview());
        }

        throw new UnsupportedOperationException("Only 'file' and 'preview' is allowed (current: '" + type + "').");
    }

    @GET
    @Path("/{type}/{nameSpace}/{name}")
    @Operation(
            summary = "Retrieves the file",
            description = "Retrieves the file by Name Space and Name"
    )
    public Response resource(
            @Schema(
                    description = "The requested file type (file or preview)",
                    required = true,
                    enumeration = {"file", "preview"},
                    example = "file"
            )
            @PathParam("type") @NotNull final String type,
            @Schema(
                    description = "The name space of the file.",
                    required = true,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH,
                    pattern = HasName.VALID_NAME_PATTERN,
                    example = "default"
            )
            @Length(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
            @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
            @PathParam("nameSpace")
            @NotNull final String nameSpace,
            @Schema(
                    description = "The name of the file.",
                    required = true,
                    example = HasName.VALID_NAME_EXAMPLE,
                    pattern = HasName.VALID_NAME_PATTERN,
                    minLength = HasName.VALID_NAME_MIN_LENGTH,
                    maxLength = HasName.VALID_NAME_MAX_LENGTH
            )
            @Length(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
            @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
            @PathParam("name")
            @NotNull final String name
    ) {
        Optional<JPAFile> data = repository.findByNameSpaceAndName(nameSpace, name);

        checkDataFound(data.isEmpty(), String.format("No file with name space '%s' and name '%s' found.", nameSpace, name));

        Optional<JPAFileData> file = retrieveFileOrPreview(data.get(), type);
        return returnFile(data.get(), file);
    }

    private void checkDataFound(boolean empty, String message) {
        if (empty) {
            throw new NotFoundException(message);
        }
    }

    private Response returnFile(
            @NotNull final JPAFile file,
            @NotNull final Optional<JPAFileData> fileData
    ) {
        checkAccess(file);

        if (fileData.isEmpty()) {
            throw new NotFoundException("Not found");
        }

        JPAFileData data = fileData.get();

        log.info("Delivering File. file={}", fileData);
        return Response
                .ok(data.getData(), data.getMediaType())
                .header("X-File-Metadata-NameSpace", file.getNameSpace())
                .header("X-File-Metadata-Name", file.getName())
                .header("X-File-Metadata-Uid", file.getId())
                .header("X-File-Owner", file.getOwner())
                .header("X-File-Group", file.getGroup())
                .header("X-File-Name", data.getName())
                .build();
    }

    private void checkAccess(JPAFile data) {
        if (!data.to().hasAccess("./.", Set.of(), File.READ)) {
            throw new ForbiddenException("Forbidden");
        }
    }
}
