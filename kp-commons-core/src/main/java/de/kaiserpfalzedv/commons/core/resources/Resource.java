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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Resource -- A generic resource holding a single data set.
 *
 * @param <D> The data set provided by this resource.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 * @version 2.0.2  2021-01-04
 */
@MappedSuperclass
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"metadata", "spec", "status"})
public class Resource<D extends Serializable> implements ResourcePointer {
    @Embedded
    @Schema(
            name = "metadata",
            description = "Technical data to the resource.",
            required = true
    )
    @NonNull
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


    @Embedded
    @Schema(
            name = "status",
            description = "The status of the resource (containing the history).",
            nullable = true
    )
    @Builder.Default
    protected Status status = null;

    /**
     * @return the display name of the resource
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public String getDisplayName() {
        return String.format("%s/%s/%s/%s", getKind(), getApiVersion(), getNameSpace(), getName());
    }


    @BsonIgnore
    @JsonIgnore
    @Transient
    @Override
    public String getKind() {
        return getMetadata().getKind();
    }

    @BsonIgnore
    @JsonIgnore
    @Transient
    @Override
    public String getApiVersion() {
        return getMetadata().getApiVersion();
    }

    @BsonIgnore
    @JsonIgnore
    @Transient
    @Override
    public String getNameSpace() {
        return getMetadata().getNameSpace();
    }

    @BsonIgnore
    @JsonIgnore
    @Transient
    @Override
    public String getName() {
        return getMetadata().getName();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public UUID getUid() {
        return metadata.getUid();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public Integer getGeneration() {
        return metadata.getGeneration();
    }


    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<D> getData() {
        return Optional.ofNullable(spec);
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<Status> getState() {
        return Optional.ofNullable(status);
    }


    synchronized public Resource<D> increaseGeneration() {
        return toBuilder()
                .withMetadata(getMetadata().increaseGeneration())
                .build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Resource<D> clone() {
        return toBuilder().build();
    }
}
