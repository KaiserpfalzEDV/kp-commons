/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.resources.DefaultResourceSpec;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.ws.rs.core.MediaType;

/**
 * The basic data for every user.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0  2021-05-24
 */
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = FileData.FileDataBuilder.class)
@Schema(description = "Managed file.")
public class FileData extends DefaultResourceSpec {
    @Builder.Default
    @Schema(description = "A description of the file.", nullable = true, example = "File with accounting data for the month.")
    private final String description = null;

    @Builder.Default
    @Schema(description = "Mediatype of the file.", example = MediaType.APPLICATION_OCTET_STREAM)
    private final String mediatype = null;

    @Builder.Default
    @Schema(description = "Base64 encoded content of the file.", minLength = 1, example = "RGFzIGhpZXIgaXN0IGVpbmZhY2ggbnVyIGVpbiBCZWlzcGllbGZpbGUK")
    private final String data = "";
}
