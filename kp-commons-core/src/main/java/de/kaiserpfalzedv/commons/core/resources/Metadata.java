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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Metadata -- common data for every resource of the system.
 * <p>
 * Default values for the lombok builder are set in {@link MetadataBuilder}.
 *
 * @author klenkes74 {@literal <rlichit@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 * @version 2.0.2 2022-01-04
 */
@Embeddable
@Builder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Metadata.MetadataBuilder.class)
@JsonPropertyOrder({"owner,created,deleted,annotations,labels"})
@Schema(
        name = "ResourceMetadata",
        description = "The metadata of a resource."
)
public class Metadata implements Serializable, Cloneable {
    @Version
    @ToString.Include
    @Schema(
            name = "generation",
            description = "The generation of this object. Every change adds 1.",
            required = true,
            example = "0",
            defaultValue = "0",
            minimum = "0"
    )
    @Builder.Default
    private Integer generation = 0;

    @Schema(
            name = "owner",
            description = "The owning resource. This is a sub-resource or managed resource of the given address.",
            nullable = true,
            implementation = ResourcePointer.class
    )
    @ToString.Exclude
    @Builder.Default
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "kind", column = @Column(name = "OWNER_KIND", length = 100)),
            @AttributeOverride(name = "apiVersion", column = @Column(name = "OWNER_API_VERSION", length = 100)),
            @AttributeOverride(name = "nameSpace", column = @Column(name = "OWNER_NAMESPACE", length = 100)),
            @AttributeOverride(name = "name", column = @Column(name = "OWNER_NAME", length = 100)),
    })
    private Pointer owner = null;

    @Schema(
            name = "created",
            description = "The timestamp of resource creation.",
            required = true,
            example = "2022-01-04T21:01:00.000000Z",
            defaultValue = "now"
    )
    @Builder.Default
    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, updatable = false)
    protected OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "modified",
            description = "The timestamp of the last change.",
            required = true,
            example = "2022-01-04T21:01:00.000000Z",
            defaultValue = "now"
    )
    @Builder.Default
    @UpdateTimestamp
    @Column(name = "MODIFIED", nullable = false)
    protected OffsetDateTime modified = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "deleted",
            description = "The timestamp of object deletion. Marks an object to be deleted.",
            nullable = true,
            example = "2022-01-04T21:01:00.000000Z",
            defaultValue = "null"
    )
    @Builder.Default
    @Column(name = "DELETED")
    private OffsetDateTime deleted = null;

    @Schema(
            name = "annotations",
            description = "A set of annotations to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @ToString.Include
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="NAME")
    @Column(name="VALUE")
    @CollectionTable(name="ANNOTATIONS", joinColumns=@JoinColumn(name="ID"))
    private HashMap<String, String> annotations = new HashMap<>();

    @Schema(
            name = "labels",
            description = "A set of labels to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @ToString.Include
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="NAME")
    @Column(name="VALUE")
    @CollectionTable(name="LABELS", joinColumns=@JoinColumn(name="ID"))
    private Map<String, String> labels = new HashMap<>();


    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<OffsetDateTime> getDeletionTimestamp() {
        return Optional.ofNullable(deleted);
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getOwningResource() {
        return Optional.ofNullable(owner);
    }

    /**
     * Checks if there is an annotation for this name.
     *
     * @param name the name of the annotation.
     * @return If there is an annotation for this name.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public boolean isAnnotated(@NotNull final String name) {
        return getAnnotations().containsKey(name);
    }

    /**
     * Returns the value of the annotation.
     *
     * @param name Annotation name to retrieve
     * @return The value of the annotation.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<String> getAnnotation(@NotNull final String name) {
        return Optional.ofNullable(getAnnotations().get(name));
    }

    /**
     * Checks if there is a label with a special name.
     *
     * @param name The name of the label.
     * @return If the label is there.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public boolean isLabeled(final String name) {
        return getLabels().containsKey(name);
    }

    /**
     * Returns the value of the label.
     *
     * @param name Label name to retrieve.
     * @return The value of the label.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<String> getLabel(@NotNull final String name) {
        return Optional.ofNullable(getLabels().get(name));
    }

    public Metadata increaseGeneration() {
        return toBuilder()
                .withGeneration(generation + 1)
                .build();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Metadata clone() {
        return toBuilder().build();
    }
}
