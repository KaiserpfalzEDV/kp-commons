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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Metadata -- The metadata of some objects.
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface Metadata extends HasUid, HasTimestamps, Cloneable {
  /**
   * Returns the self link of the resource.
   *
   * @return The display name of the resource.
   */
  @Schema(
      name = "selfLink", description = "The local part of the URL to retrieve the resource.",
      nullable = true, readOnly = true,
      pattern = "/<prefix (default: domain)>/" + HasName.VALID_NAME_PATTERN + "/"
                + HasApiVersion.VALID_VERSION_PATTERN + "/" + HasName.VALID_NAME_PATTERN + "/"
                + HasName.VALID_NAME_PATTERN, minLength = 19, maxLength = 318
  )
  @NotNull
  @JsonProperty(value = "selfLink", access = JsonProperty.Access.READ_ONLY)
  default String getSelfLink() {
    String prefix = removeTrailingSlash(getSelfLinkPrefix());
    
    return String.format(
        "%s/%s/%s/%s/%s", prefix, this.getKind().toLowerCase(Locale.getDefault()),
        this.getApiVersion().toLowerCase(Locale.getDefault()), this.getNameSpace(), this.getName()
    );
  }
  
  /**
   * Removes the trailing slash from the prefix if it exists.
   *
   * @param prefix the prefix to process.
   * @return the prefix without a trailing slash.
   */
  @NotNull
  @JsonIgnore
  default String removeTrailingSlash(@NotNull final String prefix) {
    return prefix.endsWith("/") ? prefix.substring(1, prefix.length() - 1) : prefix;
  }
  
  /**
   * Returns the prefix for the self link.
   *
   * @return The prefix for the self link.
   */
  @NotNull
  @JsonIgnore
  default String getSelfLinkPrefix() {
    return "/domain";
  }
  
  /**
   * Returns the kind of the resource.
   *
   * @return The kind of the resource.
   */
  @JsonIgnore
  default String getKind() {
    return this.getIdentity().getKind();
  }
  
  /**
   * Returns the API version of the resource.
   *
   * @return The API version of the resource.
   */
  @JsonIgnore
  default String getApiVersion() {
    return this.getIdentity().getApiVersion();
  }
  
  /**
   * Returns the namespace of the resource.
   *
   * @return The namespace of the resource.
   */
  @JsonIgnore
  default String getNameSpace() {
    return this.getIdentity().getNameSpace();
  }
  
  /**
   * Returns the name of the resource.
   *
   * @return The name of the resource.
   */
  @JsonIgnore
  default String getName() {
    return this.getIdentity().getName();
  }
  
  /**
   * Returns the identity of the resource.
   *
   * @return The identity of the resource.
   */
  Pointer getIdentity();
  
  /**
   * Returns the deletion timestamp of the resource.
   *
   * @return The deletion timestamp of the resource, if it is deleted.
   */
  @JsonIgnore
  Optional<OffsetDateTime> getDeletionTimestamp();
  
  /**
   * Returns the owning resource of this metadata.
   *
   * @return The owning resource of this metadata, if it exists.
   */
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
   * Returns the annotations of this resource.
   *
   * @return The annotations of the resource.
   */
  Map<String, String> getAnnotations();
  
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
   * Returns the labels of the resource.
   *
   * @return the labels of the resource.
   */
  Map<String, String> getLabels();
  
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
  
  /**
   * Increases the generation of the metadata.
   *
   * @return The updated metadata with an increased generation.
   */
  Metadata increaseGeneration();
  
  /**
   * Returns the generation of the metadata.
   *
   * @return The generation of the metadata.
   */
  Integer getGeneration();
  
  /**
   * Returns the owner of the resource.
   *
   * @return The owner of the resource, if it exists.
   */
  Pointer getOwner();
}
