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
import de.kaiserpfalzedv.commons.core.api.TimeStampPattern;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.*;
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
@SuppressWarnings("unused")
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
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
    @NotNull
    private Pointer identity;

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
    @NotNull
    private UUID uid = UUID.randomUUID();

    @Schema(
            name = "generation",
            description = "The generation of this object. Every change adds 1.",
            required = true,
            example = "0",
            defaultValue = "0",
            minimum = "0",
            maxItems = Integer.MAX_VALUE
    )
    @ToString.Include
    @Builder.Default
    @NotNull
    @Min(value = 0, message = "The generation must be at least 0.")
    @Max(value = Integer.MAX_VALUE, message = "The generation must not be bigger than " + Integer.MAX_VALUE + ".")
    private Integer generation = 0;

    @Schema(
            name = "owner",
            description = "The owning resource. This is a sub-resource or managed resource of the given address.",
            nullable = true,
            implementation = ResourcePointer.class
    )
    @Builder.Default
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
    @Size(min = TimeStampPattern.VALID_LENGTH, max = TimeStampPattern.VALID_LENGTH, message = TimeStampPattern.VALID_LENGTH_MSG)
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
    @Size(min = TimeStampPattern.VALID_LENGTH, max = TimeStampPattern.VALID_LENGTH, message = TimeStampPattern.VALID_LENGTH_MSG)
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
    private OffsetDateTime deleted = null;

    @Schema(
            name = "annotations",
            description = "A set of annotations to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @Builder.Default
    private Map<String, String> annotations = new HashMap<>();

    @Schema(
            name = "labels",
            description = "A set of labels to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @Builder.Default
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
    @JsonProperty(value = "selfLink", access = JsonProperty.Access.READ_ONLY)
    public String getSelfLink() {
        return String.format("/api/%s/%s/%s/%s",
                getKind().toLowerCase(), getApiVersion().toLowerCase(),
                getNameSpace(), getName()
        );
    }


    @JsonIgnore
    public Optional<OffsetDateTime> getDeletionTimestamp() {
        return Optional.ofNullable(deleted);
    }

    @JsonIgnore
    public Optional<ResourcePointer> getOwningResource() {
        return Optional.ofNullable(owner);
    }

    /**
     * Checks if there is an annotation for this name.
     *
     * @param name the name of the annotation.
     * @return If there is an annotation for this name.
     */
    @JsonIgnore
    public boolean isAnnotated(@NotNull final String name) {
        return getAnnotations().containsKey(name);
    }

    /**
     * Returns the value of the annotation.
     *
     * @param name Annotation name to retrieve
     * @return The value of the annotation.
     */
    @JsonIgnore
    public Optional<String> getAnnotation(@NotNull final String name) {
        return Optional.ofNullable(getAnnotations().get(name));
    }

    /**
     * Checks if there is a label with a special name.
     *
     * @param name The name of the label.
     * @return If the label is there.
     */
    @JsonIgnore
    public boolean isLabeled(final String name) {
        return getLabels().containsKey(name);
    }

    /**
     * Returns the value of the label.
     *
     * @param name Label name to retrieve.
     * @return The value of the label.
     */
    
    @JsonIgnore
    public Optional<String> getLabel(@NotNull final String name) {
        return Optional.ofNullable(getLabels().get(name));
    }

    public Metadata increaseGeneration() {
        return toBuilder()
                .generation(generation + 1)
                .build();
    }

    
    @JsonIgnore
    public String getKind() {
        return identity.getKind();
    }

    
    @JsonIgnore
    public String getApiVersion() {
        return identity.getApiVersion();
    }

    
    @JsonIgnore
    public String getNameSpace() {
        return identity.getNameSpace();
    }

    
    @JsonIgnore
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
                .identity(
                        Pointer.builder()
                                .kind(kind)
                                .apiVersion(apiVersion)
                                .nameSpace(nameSpace)
                                .name(name)
                                .build()
                );
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Metadata clone() {
        return toBuilder().build();
    }
}
