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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.boot.MetadataBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.Metadata;
import de.kaiserpfalzedv.commons.api.resources.Pointer;
import de.kaiserpfalzedv.commons.api.resources.TimeStampPattern;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Metadata -- common data for every resource of the system.
 * <p>
 * Default values for the lombok builder are set in {@link MetadataBuilder}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 * @version 2.0.2 2022-01-04
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REF2", justification = "Use of lombok provided builder.")
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
public class MetadataImpl implements Metadata {
    /** serial class version */
    private static final long serialVersionUID = 0L;

    @Schema(
            name = "identity",
            description = "This is the identity of the resource.",
            implementation = Pointer.class
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @NotNull
    private de.kaiserpfalzedv.commons.core.resources.PointerImpl identity;

    @Schema(
            name = "uid",
            description = "The unique identifier of this resource",
            required = true,
            minLength = HasId.MIN_LENGTH,
            maxLength = HasId.MAX_LENGTH,
            pattern = HasId.VALID_ID_PATTERN,
            example = HasId.VALID_ID_EXAMPLE,
            defaultValue = "random UUID"
    )
    @ToString.Include
    @EqualsAndHashCode.Include
    @Builder.Default
    @NotNull
    private final UUID uid = UUID.randomUUID();

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
    private final Integer generation = 0;

    @Schema(
            name = "owner",
            description = "The owning resource. This is a sub-resource or managed resource of the given address.",
            nullable = true,
            implementation = Pointer.class
    )
    @Builder.Default
    private final de.kaiserpfalzedv.commons.core.resources.PointerImpl owner = null;

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
    private final OffsetDateTime deleted = null;

    @Schema(
            name = "annotations",
            description = "A set of annotations to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @Builder.Default
    @SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
    private final Map<String, String> annotations = new HashMap<>();

    @Schema(
            name = "labels",
            description = "A set of labels to this resource.",
            nullable = true,
            minItems = 0,
            maxItems = 256
    )
    @Builder.Default
    @SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
    private final Map<String, String> labels = new HashMap<>();


    @Override
    @JsonIgnore
    public Optional<OffsetDateTime> getDeletionTimestamp() {
        return Optional.ofNullable(this.deleted);
    }

    @Override
    @JsonIgnore
    public Optional<de.kaiserpfalzedv.commons.api.resources.Pointer> getOwningResource() {
        return Optional.ofNullable(this.owner);
    }

    @Override
    public MetadataImpl increaseGeneration() {
        return this.toBuilder()
                .generation(this.generation + 1)
                .build();
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
    public static MetadataImplBuilder of(
            final String kind,
            final String apiVersion,
            final String nameSpace,
            final String name
    ) {
        return MetadataImpl.builder()
                .identity(
                        de.kaiserpfalzedv.commons.core.resources.PointerImpl.builder()
                                .kind(kind)
                                .apiVersion(apiVersion)
                                .nameSpace(nameSpace)
                                .name(name)
                                .build()
                );
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @SuppressFBWarnings(value = "CN_IDIOM_NO_SUPER_CALL", justification = "Using the lombok builder.")
    @Override
    public MetadataImpl clone() {
        return this.toBuilder().build();
    }
}
