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
import de.kaiserpfalzedv.commons.api.store.StoreService;

import java.io.Serializable;
import java.util.*;

/**
 * <p>DefaultResourceSpec -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface DefaultResourceSpec extends Serializable, Cloneable {
    /**
     * Returns a property.
     *
     * @param key The unique key of the property within the user dataset.
     * @return The property saved with the user.
     */
    @JsonIgnore
    default Optional<String> getProperty(final String key) {
        return Optional.ofNullable(getProperties().get(key));
    }


    /**
     * Returns an array of property names which should be saved by a {@link StoreService}
     * implementation. You should really overwrite it when needed.
     *
     * @return the names of the default properties of this resource.
     */
    @JsonIgnore
    default String[] getDefaultProperties() {
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
    @JsonIgnore
    default Optional<Pointer> getResourcePointer(String key) {
        try {
            String property = getProperty(key).orElseThrow();

            return Optional.of(convertStringToResourcePointer(property));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    @JsonIgnore
    default List<Pointer> getResourcePointers(String key) {
        try {
            String property = getProperty(key).orElseThrow();

            String[] data = property.split(",");

            ArrayList<Pointer> result = new ArrayList<>(data.length);
            for (String p : data) {
                result.add(convertStringToResourcePointer(p));
            }

            return result;
        } catch (NoSuchElementException e) {
            return new ArrayList<>();
        }
    }


    @JsonIgnore
    Pointer convertStringToResourcePointer(String property);

    /**
     * Saves a resource pointer as property.
     *
     * @param key     The name of the property.
     * @param pointer the pointer to save.
     */
    @JsonIgnore
    default void saveResourcePointer(String key, Pointer pointer) {
        if (pointer != null) {
            String data = convertResourcePointerToString(pointer);

            getProperties().put(key, data);
        }
    }

    default String convertResourcePointerToString(Pointer pointer) {
        return new StringJoiner("/")
                .add(pointer.getKind())
                .add(pointer.getApiVersion())
                .add(pointer.getNameSpace())
                .add(pointer.getName())
                .toString();
    }

    @JsonIgnore
    void saveResourcePointers(String key, Collection<Pointer> pointers);

    Map<String, String> getProperties();
}
