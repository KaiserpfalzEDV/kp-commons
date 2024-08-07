/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.api.resources;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

/**
 * <p>Metadata -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface Metadata extends Serializable, Cloneable {
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
    default String getSelfLink() {
        return String.format("/api/%s/%s/%s/%s",
                this.getKind().toLowerCase(Locale.getDefault()), this.getApiVersion().toLowerCase(Locale.getDefault()),
                this.getNameSpace(), this.getName()
        );
    }

    @JsonIgnore
    Optional<OffsetDateTime> getDeletionTimestamp();

    @JsonIgnore
    Optional<Pointer> getOwningResource();

    /**
     * Checks if there is an annotation for this name.
     *
     * @param name the name of the annotation.
     * @return If there is an annotation for this name.
     */
    @JsonIgnore
    default boolean isAnnotated(@NotNull final String name) {
        return this.getAnnotations().containsKey(name);
    }

    /**
     * Returns the value of the annotation.
     *
     * @param name Annotation name to retrieve
     * @return The value of the annotation.
     */
    @JsonIgnore
    default Optional<String> getAnnotation(@NotNull final String name) {
        return Optional.ofNullable(this.getAnnotations().get(name));
    }

    /**
     * Checks if there is a label with a special name.
     *
     * @param name The name of the label.
     * @return If the label is there.
     */
    @JsonIgnore
    default boolean isLabeled(final String name) {
        return this.getLabels().containsKey(name);
    }

    /**
     * Returns the value of the label.
     *
     * @param name Label name to retrieve.
     * @return The value of the label.
     */

    @JsonIgnore
    default Optional<String> getLabel(@NotNull final String name) {
        return Optional.ofNullable(this.getLabels().get(name));
    }

    Metadata increaseGeneration();

    @JsonIgnore
    default String getKind() {
        return this.getIdentity().getKind();
    }

    @JsonIgnore
    default String getApiVersion() {
        return this.getIdentity().getApiVersion();
    }

    @JsonIgnore
    default String getNameSpace() {
        return this.getIdentity().getNameSpace();
    }

    @JsonIgnore
    default String getName() {
        return this.getIdentity().getName();
    }

    Pointer getIdentity();

    UUID getUid();

    Integer getGeneration();

    Pointer getOwner();

    OffsetDateTime getCreated();

    OffsetDateTime getModified();

    OffsetDateTime getDeleted();

    Map<String, String> getAnnotations();

    Map<String, String> getLabels();
}
