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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.api.TimeStampPattern;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Metadata.MetadataBuilder.class)
@JsonPropertyOrder({"identity,uid,generation,owner,created,deleted,annotations,labels,selfLink"})
@Schema(
        name = "ResourceMetadata",
        description = "The metadata of a resource."
)
public class Metadata implements Serializable, Cloneable {
    @Schema(
            name = "identity",
            description = "This is the identity of the resource.",
            implementation = ResourcePointer.class
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @NonNull
    @Embedded
    private Pointer identity;

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
            minLength = HasId.VALID_UUID_LENGTH,
            maxLength = HasId.VALID_UUID_LENGTH,
            pattern = HasId.VALID_UUID_PATTERN,
            example = HasId.VALID_UUID_EXAMPLE,
            defaultValue = "random UUID"
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @Builder.Default
    @NonNull
    private UUID uid = UUID.randomUUID();

    @Version
    @ToString.Include
    @Schema(
            name = "generation",
            description = "The generation of this object. Every change adds 1.",
            required = true,
            example = "0",
            defaultValue = "0",
            minimum = "0",
            maxItems = Integer.MAX_VALUE
    )
    @Builder.Default
    @NonNull
    @Range(min = 0, max = Integer.MAX_VALUE, message = "The generation has to be between 0 and " + Integer.MAX_VALUE)
    private Integer generation = 0;

    @Schema(
            name = "owner",
            description = "The owning resource. This is a sub-resource or managed resource of the given address.",
            nullable = true,
            implementation = ResourcePointer.class
    )
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
            defaultValue = "now",
            example = TimeStampPattern.VALID_EXAMPLE,
            pattern = TimeStampPattern.VALID_PATTERN,
            minLength = TimeStampPattern.VALID_LENGTH,
            maxLength = TimeStampPattern.VALID_LENGTH
    )
    @Builder.Default
    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, updatable = false)
    @Length(min = TimeStampPattern.VALID_LENGTH, max = TimeStampPattern.VALID_LENGTH, message = TimeStampPattern.VALID_LENGTH_MSG)
    @Pattern(regexp = TimeStampPattern.VALID_PATTERN, message = TimeStampPattern.VALID_PATTERN_MSG)
    protected OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "modified",
            description = "The timestamp of the last change.",
            required = true,
            defaultValue = "now",
            example = TimeStampPattern.VALID_EXAMPLE,
            pattern = TimeStampPattern.VALID_PATTERN,
            minLength = TimeStampPattern.VALID_LENGTH,
            maxLength = TimeStampPattern.VALID_LENGTH
    )
    @Builder.Default
    @UpdateTimestamp
    @Column(name = "MODIFIED", nullable = false)
    @Length(min = TimeStampPattern.VALID_LENGTH, max = TimeStampPattern.VALID_LENGTH, message = TimeStampPattern.VALID_LENGTH_MSG)
    @Pattern(regexp = TimeStampPattern.VALID_PATTERN, message = TimeStampPattern.VALID_PATTERN_MSG)
    protected OffsetDateTime modified = OffsetDateTime.now(ZoneOffset.UTC);

    @Schema(
            name = "deleted",
            description = "The timestamp of object deletion. Marks an object to be deleted.",
            nullable = true,
            defaultValue = "null",
            example = TimeStampPattern.VALID_EXAMPLE,
            pattern = TimeStampPattern.VALID_PATTERN,
            minLength = TimeStampPattern.VALID_LENGTH,
            maxLength = TimeStampPattern.VALID_LENGTH
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
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "ANNOTATIONS", joinColumns = @JoinColumn(name = "ID"))
    private Map<String, String> annotations = new HashMap<>();

    @Schema(
            name = "labels",
            description = "A set of labels to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "LABELS", joinColumns = @JoinColumn(name = "ID"))
    private Map<String, String> labels = new HashMap<>();


    /**
     * @return The display name of the resource.
     */
    @Schema(
            name = "selfLink",
            description = "The local part of the URL to retrieve the resource.",
            nullable = true,
            readOnly = true,
            example = "/api/resource/v1/default/name",
            pattern = "/api/" + HasName.VALID_NAME_PATTERN
                    + "/" + HasApiVersion.VALID_VERSION_PATTERN
                    + "/" + HasName.VALID_NAME_PATTERN
                    + "/" + HasName.VALID_NAME_PATTERN,
            minLength = 19,
            maxLength = 318
    )
    @BsonProperty(value = "selfLink")
    @JsonProperty(value = "selfLink", access = JsonProperty.Access.READ_ONLY)
    public String getSelfLink() {
        return String.format("/api/%s/%s/%s/%s",
                getKind().toLowerCase(), getApiVersion().toLowerCase(),
                getNameSpace(), getName()
        );
    }


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

    @Transient
    @JsonIgnore
    @BsonIgnore
    public String getKind() {
        return identity.getKind();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public String getApiVersion() {
        return identity.getApiVersion();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public String getNameSpace() {
        return identity.getNameSpace();
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public String getName() {
        return identity.getName();
    }

    /**
     * Generates a metadata builder with the given identity.
     *
     * @param kind       the kind of resource.
     * @param apiVersion the api version of the resource.
     * @param nameSpace  the namespace of the resource.
     * @param name       the name of the resource.
     * @return A metadata builder for adding the other metadata.
     */
    public static MetadataBuilder of(
            final String kind,
            final String apiVersion,
            final String nameSpace,
            final String name
    ) {
        return Metadata.builder()
                .withIdentity(
                        Pointer.builder()
                                .withKind(kind)
                                .withApiVersion(apiVersion)
                                .withNameSpace(nameSpace)
                                .withName(name)
                                .build()
                );
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Metadata clone() {
        return toBuilder().build();
    }
}
