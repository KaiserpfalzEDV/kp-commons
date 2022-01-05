/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
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
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"kind","apiVersion","nameSpace","name","selfLink","metadata","spec","status"})
public class Resource<D extends Serializable> implements ResourcePointer {
    @SuppressWarnings("deprecation")
    @Id
    @BsonId
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    @Schema(
            name = "uid",
            description = "The unique identifier of this resource",
            required = true,
            example = "caae022d-5728-4cb2-9245-b8c1ea03e380",
            defaultValue = "random UUID",
            minLength = 36,
            maxLength = 36
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID uid = UUID.randomUUID();

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
    private String name;

    @javax.persistence.Transient
    @Schema(
            name = "selfLink",
            description = "The local part of the URL to retrieve the resource.",
            nullable = true,
            readOnly = true,
            example = "/api/v1/Resource/default/name",
            minLength = 8,
            maxLength = 100
    )
    public String getSelfLink() {
        return String.format("/api/%s/%s/%s", getApiVersion(), getKind(), getNameSpace(), getName());
    }

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
    public int hashCode() {
        return Objects.hash(getKind(), getNameSpace(), getName());
    }


    @Embedded
    @Schema(
            name = "metadata",
            description = "Technical data to the resource.",
            required = true
    )
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
                .withMetadata(
                        getMetadata().toBuilder()
                                .withGeneration(getGeneration() + 1)
                                .build()
                )
                .build();
    }


    @Override
    public Resource<D> clone() {
        return Resource.<D>builder()
                .withKind(getKind())
                .withApiVersion(getApiVersion())
                .withNameSpace(getNameSpace())
                .withName(getName())
                .withMetadata(getMetadata())
                .withSpec(getSpec())
                .withStatus(getStatus())
                .build();
    }
}
