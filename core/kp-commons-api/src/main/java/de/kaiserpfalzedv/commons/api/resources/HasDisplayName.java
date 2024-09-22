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

package de.kaiserpfalzedv.commons.api.resources;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * HasDisplayName -- The object has a name to display in UIs.
 * 
 * The display name can be combined but should not exceed 100 characters.
 * The method {@link #limitTo100Characters(String)} can be used for implementations to cut of too long display names.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2022-01-16
 * @since 0.1.0  2021-04-18
 */
public interface HasDisplayName extends HasName {
    /**
     * Default implemetnation of getDisplayName().
     * 
     * @return the result of {@link #getName()}.
     */
    @Schema(
            name = "displayName",
            description = "The name of a resource to be displayed to human users.",
            pattern = VALID_NAME_PATTERN,
            minLength = VALID_NAME_MIN_LENGTH,
            maxLength = VALID_NAME_MAX_LENGTH
    )
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH, message = VALID_NAME_LENGTH_MSG)
    @Pattern(regexp = VALID_NAME_PATTERN, message = VALID_NAME_PATTERN_MSG)
    default String getDisplayName() {
        return limitTo100Characters(getName());
    }

    /**
     * Limits the length of the content to 100 by taking at most 97 characters length of the content and then adding 3 dots.
     * 
     * @param content the content to be cut off.
     * @return the cut of content.
     */
    default String limitTo100Characters(String content) {
        return content.length() <= 100 ? content : content.substring(0, 96) + "...";
    }
}
