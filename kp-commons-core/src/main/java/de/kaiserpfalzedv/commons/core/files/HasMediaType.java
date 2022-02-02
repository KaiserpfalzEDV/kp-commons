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

package de.kaiserpfalzedv.commons.core.files;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * HasMediaType -- The object has a name.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2022-01-16
 * @since 2.0.2  2022-01-16
 */
public interface HasMediaType {
    /**
     * This is the pattern for a valid media type.
     */
    String VALID_MEDIATYPE_PATTERN = "^[a-zA-Z0-9]+/[a-zA-Z0-9]+(;[-a-zA-Z0-9])?$";
    int VALID_MEDIATYPE_MIN_LENGTH = 3;
    int VALID_MEDIATYPE_MAX_LENGTH = 100;

    @Schema(
            name = "mediaType",
            description = "A valid mediatype.",
            pattern = VALID_MEDIATYPE_PATTERN,
            minLength = VALID_MEDIATYPE_MIN_LENGTH,
            maxLength = VALID_MEDIATYPE_MAX_LENGTH,
            example = "text/plain"
    )
    @NotBlank(message = "The media type may not be empty.")
    @Size(min = VALID_MEDIATYPE_MIN_LENGTH, max = VALID_MEDIATYPE_MAX_LENGTH, message = "The media type is either too short or too long.")
    @Pattern(regexp = VALID_MEDIATYPE_PATTERN, message = "The mediatype does not match a  valid Media Type.")
    String getMediaType();
}
