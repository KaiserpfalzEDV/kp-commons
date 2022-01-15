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

package de.kaiserpfalzedv.commons.core.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ResourcePointer -- A single resource definition pointing to a unique resource on the server.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Embeddable
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonDeserialize(builder = Pointer.PointerBuilder.class)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"kind", "apiVersion", "namespace", "name", "selfLink"})
@Schema(
        description = "A full pointer to a resource.",
        example = "{" +
                "\n\t\"kind\": \"Resource\"," +
                "\n\t\"apiVersion\": \"v1\"," +
                "\n\t\"nameSpace\": \"namespace\"," +
                "\n\t\"name\": \"name\"," +
                "\n\t\"selfLink\": \"/api/v1/Resource/namespace/name\"" +
                "\n}"
)
public class Pointer implements ResourcePointer {
    @Column(name = "KIND", length = 100, nullable = false, updatable = false)
    @Schema(
            name = "kind",
            description = "The type of the resource",
            required = true,
            example = "Resource",
            defaultValue = "Resource",
            minLength = 1,
            maxLength = 100
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    private String kind;

    @Column(name = "API_VERSION", length = 100, nullable = false, updatable = false)
    @Schema(
            name = "apiVersion",
            description = "The version of this resource",
            required = true,
            example = "v1",
            defaultValue = "v1",
            minLength = 3,
            maxLength = 100
    )
    @Builder.Default
    private String apiVersion = "v1";

    @Column(name = "NAMESPACE", length = 100, nullable = false)
    @Schema(
            name = "nameSpace",
            description = "The namespace (group) of this resource",
            required = true,
            example = "default",
            defaultValue = "default",
            minLength = 1,
            maxLength = 100
    )
    @Builder.Default
    @ToString.Include
    @EqualsAndHashCode.Include
    private String nameSpace = "default";

    @Column(name = "NAME", length = 100, nullable = false)
    @Schema(
            name = "name",
            description = "The unique name of this resource within the namespace",
            required = true,
            example = "name",
            minLength = 1,
            maxLength = 100
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pointer)) return false;
        Pointer that = (Pointer) o;
        return getKind().equals(that.getKind())
                && getNameSpace().equals(that.getNameSpace())
                && getName().equals(that.getName());
    }

    @Override
    public Pointer clone() {
        return toBuilder().build();
    }
}
