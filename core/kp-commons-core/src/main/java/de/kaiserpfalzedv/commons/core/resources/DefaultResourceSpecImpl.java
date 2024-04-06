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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.kaiserpfalzedv.commons.api.resources.DefaultResourceSpec;
import de.kaiserpfalzedv.commons.api.resources.Pointer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * The basic data for every resource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@SuppressWarnings({"unused"})
@SuppressFBWarnings(value = "EI_EXPOSE_REF2", justification = "Use of lombok provided builder.")
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(name = "DefaultResourceSpec", description = "A standardized resource.")
public class DefaultResourceSpecImpl implements DefaultResourceSpec {
    /** serial version of this class. */
    private static final long serialVersionUID = 0L;

    @SuppressWarnings("FieldMayBeFinal")
    @SuppressFBWarnings(value = "EI_EXPOSE_REF2", justification = "Use of lombok provided builder.")
    @Schema(name = "properties", description = "A map of plugin properties for spec.")
    @Builder.Default
    @ToString.Include
    @EqualsAndHashCode.Include
    private Map<String, String> properties = new HashMap<>();

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    @JsonIgnore
    public Pointer convertStringToResourcePointer(final String property) {
        final String[] data = property.split("/", 4);
        if (data.length != 5) {
            throw new IllegalStateException("Invalid property for resource pointers: " + property);
        }

        return PointerImpl.builder()
                .kind(data[0])
                .apiVersion(data[1])

                .nameSpace(data[2])
                .name(data[3])

                .build();
    }

    @Override
    @JsonIgnore
    public void saveResourcePointers(final String key, final Collection<Pointer> pointers) {
        if (pointers != null) {
            final StringJoiner data = new StringJoiner(",");

            pointers.forEach(p -> data.add(this.convertResourcePointerToString(p)));

            this.properties.put(key, data.toString());
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @SuppressFBWarnings(value = "CN_IDIOM_NO_SUPER_CALL", justification = "We are using the lombok builder here.")
    @Override
    public DefaultResourceSpecImpl clone() {
        return this.toBuilder().build();
    }
}
