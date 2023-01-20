/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.kaiserpfalzedv.commons.api.resources.Metadata;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Resource -- A generic resource holding a single data set.
 *
 * @param <D> The data set provided by this resource.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 3.0.0  2023-01-07
 * @version 2.1.0  2022-01-16
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"metadata", "spec", "status"})
public class Resource<D extends Serializable> implements de.kaiserpfalzedv.commons.api.resources.Resource<D> {
    @Schema(
            name = "metadata",
            description = "Technical data to the resource.",
            required = true
    )
    @NotNull
    @ToString.Include
    @EqualsAndHashCode.Include
    protected Metadata metadata;


    @Schema(
            name = "spec",
            description = "The resource data itself.",
            required = true
    )
    @Builder.Default
    protected D spec = null;


    @Schema(
            name = "status",
            description = "The status of the resource (containing the history).",
            nullable = true
    )
    @Builder.Default
    protected Status status = null;


    @Override
    @JsonIgnore
    public Pointer toPointer() {
        return Pointer.builder()
                .kind(getKind())
                .apiVersion(getApiVersion())
                .nameSpace(getNameSpace())
                .nameSpace(getName())
                .build();
    }

    @JsonIgnore
    @Override
    public String getKind() {
        return getMetadata().getKind();
    }

    @JsonIgnore
    @Override
    public String getApiVersion() {
        return getMetadata().getApiVersion();
    }

    @JsonIgnore
    @Override
    public String getNameSpace() {
        return getMetadata().getNameSpace();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return getMetadata().getName();
    }

    @Override
    @JsonIgnore
    public UUID getUid() {
        return metadata.getUid();
    }

    @Override
    @JsonIgnore
    public Integer getGeneration() {
        return metadata.getGeneration();
    }


    @JsonIgnore
    public Optional<D> getData() {
        return Optional.ofNullable(spec);
    }

    @JsonIgnore
    public Optional<Status> getState() {
        return Optional.ofNullable(status);
    }

    @Override
    @JsonIgnore
    public String getSelfLink() {
        return getMetadata().getSelfLink();
    }


    @Override
    synchronized public Resource<D> increaseGeneration() {
        return toBuilder()
                .metadata(getMetadata().increaseGeneration())
                .build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Resource<D> clone() {
        return toBuilder().build();
    }
}
