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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.store.StoreService;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.*;

/**
 * The basic data for every resource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@SuppressWarnings({"unused"})
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(name = "DefaultResourceSpec", description = "A standardized resource.")
public class DefaultResourceSpec implements Serializable, Cloneable {
    @SuppressWarnings("FieldMayBeFinal")
    @Schema(name = "properties", description = "A map of plugin properties for spec.")
    @Builder.Default
    @ToString.Include
    @EqualsAndHashCode.Include
    private Map<String, String> properties = new HashMap<>();

    /**
     * Returns a property.
     *
     * @param key The unique key of the property within the user dataset.
     * @return The property saved with the user.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<String> getProperty(final String key) {
        return Optional.ofNullable(getProperties().get(key));
    }

    /**
     * Returns an array of property names which should be saved by a {@link StoreService}
     * implementation. You should really overwrite it when needed.
     *
     * @return the names of the default properties of this resource.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public String[] getDefaultProperties() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads a resource pointer from a property.
     *
     * @param key The name of the property.
     * @return The resource pointer.
     * @throws IllegalStateException    If the property can't be converted.
     * @throws IllegalArgumentException If the UUID of the pointer can't be read from the property.
     * @throws NoSuchElementException   There is no such property.
     */
    @Transient
    @JsonIgnore
    @BsonIgnore
    public Optional<ResourcePointer> getResourcePointer(final String key) {
        try {
            String property = getProperty(key).orElseThrow();

            return Optional.of(convertStringToResourcePointer(property));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    @Transient
    @JsonIgnore
    @BsonIgnore
    public List<ResourcePointer> getResourcePointers(final String key) {
        try {
            String property = getProperty(key).orElseThrow();

            String[] data = property.split(",");

            ArrayList<ResourcePointer> result = new ArrayList<>(data.length);
            for (String p : data) {
                result.add(convertStringToResourcePointer(p));
            }

            return result;
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }

    private ResourcePointer convertStringToResourcePointer(final String property) {
        String[] data = property.split("/", 4);
        if (data.length != 5) {
            throw new IllegalStateException("Invalid property for resource pointers: " + property);
        }

        return Pointer.builder()
                .kind(data[0])
                .apiVersion(data[1])

                .nameSpace(data[2])
                .name(data[3])

                .build();
    }

    /**
     * Saves a resource pointer as property.
     *
     * @param key     The name of the property.
     * @param pointer the pointer to save.
     */
    @Transient
    @JsonIgnore
    public void saveResourcePointer(final String key, final ResourcePointer pointer) {
        if (pointer != null) {
            String data = convertResourcePointerToString(pointer);

            getProperties().put(key, data);
        }
    }

    private String convertResourcePointerToString(ResourcePointer pointer) {
        return new StringJoiner("/")
                .add(pointer.getKind())
                .add(pointer.getApiVersion())
                .add(pointer.getNameSpace())
                .add(pointer.getName())
                .toString();
    }

    @Transient
    @JsonIgnore
    public void saveResourcePointers(final String key, final Collection<ResourcePointer> pointers) {
        if (pointers != null) {
            StringJoiner data = new StringJoiner(",");

            pointers.forEach(p -> data.add(convertResourcePointerToString(p)));

            properties.put(key, data.toString());
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public DefaultResourceSpec clone() {
        return toBuilder().build();
    }
}
