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

package de.kaiserpfalzedv.commons.core.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * ResourcePointer -- A single resource definition pointing to a unique resource on the server.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"kind", "apiVersion", "namespace", "name", "selfLink"})
@Schema(
        description = "A full pointer to a resource.",
        example = """
                {
                    "kind": "Resource",
                    "apiVersion": "v1",
                    "nameSpace": "namespace",
                    "name": "name",
                    "selfLink": "/api/v1/Resource/namespace/name"
                }"""
)
public class Pointer implements ResourcePointer {
    @Schema(
            name = "kind",
            description = "The type of the resource",
            required = true,
            example = HasName.VALID_NAME_EXAMPLE,
            pattern = HasName.VALID_NAME_PATTERN,
            minLength = HasName.VALID_NAME_MIN_LENGTH,
            maxLength = HasName.VALID_NAME_MAX_LENGTH
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @NotNull
    @Size(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
    private String kind;

    @Schema(
            name = "apiVersion",
            description = "The version of this resource",
            required = true,
            example = HasApiVersion.VALID_VERSION_EXAMPLE,
            defaultValue = HasApiVersion.VALID_VERSION_EXAMPLE,
            minLength = HasApiVersion.VALID_VERSION_MIN_LENGTH,
            maxLength = HasApiVersion.VALID_VERSION_MAX_LENGTH
    )
    @Builder.Default
    @Size(min = HasApiVersion.VALID_VERSION_MIN_LENGTH, max = HasApiVersion.VALID_VERSION_MAX_LENGTH, message = HasApiVersion.VALID_VERSION_LENGTH_MSG)
    @Pattern(regexp = HasApiVersion.VALID_VERSION_PATTERN, message = HasApiVersion.VALID_VERSION_PATTERN_MSG)
    private String apiVersion = HasApiVersion.VALID_VERSION_EXAMPLE;

    @Schema(
            name = "nameSpace",
            description = "The namespace (group) of this resource",
            required = true,
            example = HasName.VALID_NAME_EXAMPLE,
            pattern = HasName.VALID_NAME_PATTERN,
            minLength = HasName.VALID_NAME_MIN_LENGTH,
            maxLength = HasName.VALID_NAME_MAX_LENGTH
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @NotNull
    @Size(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
    private String nameSpace;

    @Schema(
            name = "name",
            description = "The unique name of this resource within the namespace",
            required = true,
            example = HasName.VALID_NAME_EXAMPLE,
            minLength = HasName.VALID_NAME_MIN_LENGTH,
            maxLength = HasName.VALID_NAME_MAX_LENGTH
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @Size(min = HasName.VALID_NAME_MIN_LENGTH, max = HasName.VALID_NAME_MAX_LENGTH, message = HasName.VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = HasName.VALID_NAME_PATTERN, message = HasName.VALID_NAME_PATTERN_MSG)
    private String name;


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Pointer clone() {
        return toBuilder().build();
    }
}
