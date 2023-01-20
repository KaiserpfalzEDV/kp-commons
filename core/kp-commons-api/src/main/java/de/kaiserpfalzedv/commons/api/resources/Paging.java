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
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>Paging -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface Paging extends Serializable {
    @JsonIgnore
    @Schema(hidden = true)
    @NotNull
    Paging firstPage();

    @JsonIgnore
    @Schema(hidden = true)
    @NotNull
    Paging previousPage();

    @JsonIgnore
    @Schema(hidden = true)
    @NotNull
    Paging nextPage();

    @JsonIgnore
    @Schema(hidden = true)
    @NotNull
    Paging lastPage();

    @Schema(description = "First element of total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @NotNull @Min(0) @Max(Long.MAX_VALUE)
    long getStart();

    @Schema(description = "Number of requested elements of the total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @NotNull @Min(0) @Max(Long.MAX_VALUE)
    long getSize();

    @Schema(description = "Count of elements in current page (may differ from size).", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @NotNull @Min(0) @Max(Long.MAX_VALUE)
    long getCount();

    @Schema(description = "Count of elements in total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @NotNull @Min(0) @Max(Long.MAX_VALUE)
    long getTotal();
}
