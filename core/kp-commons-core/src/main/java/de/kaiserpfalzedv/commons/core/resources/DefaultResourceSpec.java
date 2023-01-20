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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

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
public class DefaultResourceSpec implements de.kaiserpfalzedv.commons.api.resources.DefaultResourceSpec {
    @SuppressWarnings("FieldMayBeFinal")
    @Schema(name = "properties", description = "A map of plugin properties for spec.")
    @Builder.Default
    @ToString.Include
    @EqualsAndHashCode.Include
    private Map<String, String> properties = new HashMap<>();



    @JsonIgnore
    public de.kaiserpfalzedv.commons.api.resources.Pointer convertStringToResourcePointer(String property) {
        String[] data = property.split("/", 4);
        if (data.length != 5) {
            throw new IllegalStateException("Invalid property for resource pointers: " + property);
        }

        return de.kaiserpfalzedv.commons.core.resources.Pointer.builder()
                .kind(data[0])
                .apiVersion(data[1])

                .nameSpace(data[2])
                .name(data[3])

                .build();
    }

    @JsonIgnore
    public void saveResourcePointers(String key, Collection<de.kaiserpfalzedv.commons.api.resources.Pointer> pointers) {
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
