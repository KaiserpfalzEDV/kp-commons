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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

/**
 * ResourcePointer -- Identifies a single resource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2021-01-04
 * @since 2.0.0  2021-05-24
 */
public interface ResourcePointer extends Serializable, Cloneable {
    /**
     * @return The type of the resource.
     */
    String getKind();

    /**
     * @return the API version of the resource.
     */
    String getApiVersion();

    /**
     * @return The namespace of the resource.
     */
    String getNameSpace();

    /**
     * @return The name of the resource.
     */
    String getName();

    /**
     * @return The display name of the resource.
     */
    @Schema(
            name = "selfLink",
            description = "The local part of the URL to retrieve the resource.",
            nullable = true,
            readOnly = true,
            example = "/Resource/v1/default/name",
            minLength = 8,
            maxLength = 100
    )
    @BsonProperty(value = "selfLink")
    @JsonProperty(value = "selfLink", required = false, access = JsonProperty.Access.READ_ONLY)
    default String getDisplayName() {
        return String.format("/%s/%s/%s/%s", getKind(), getApiVersion(), getNameSpace(), getName());
    }

}
