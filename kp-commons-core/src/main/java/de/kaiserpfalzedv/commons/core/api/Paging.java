/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

/**
 * DispatchmentFile contains all data to a single dispatchment
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@Schema(
        description = "Paging data including calculating next/previous and first/last pages available.",
        example = "{\n" +
                "  \"start\"=0; \n" +
                "  \"size\": 100; \n" +
                "  \"count\": 83; \n" +
                "  \"total\": 83;\n" +
                "}",
        title = "Pagination data for transfer lists"
)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Paging implements Serializable {
    @Schema(description = "First element of total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @Builder.Default
    private final long start = 0;

    @Schema(description = "Number of requested elements of the total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    @Builder.Default
    private final long size = 20;

    @Schema(description = "Count of elements in current page (may differ from size).", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    private final long count;

    @Schema(description = "Count of elements in total result set.", type = SchemaType.NUMBER, minimum = "0", maximum = "9223372036854775807", required = true)
    private final long total;

    @JsonIgnore
    @Schema(hidden = true)
    public Paging firstPage() {
        return toBuilder()
                .start(0)
                .count(calculatePageSize(0))
                .build();
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Paging previousPage() {
        if (start - size <= 0) {
            return firstPage();
        }

        return toBuilder()
                .start(start - size)
                .count(calculatePageSize(start - size))
                .build();
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Paging nextPage() {
        return calculateNextPage(start + size);
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Paging lastPage() {
        return calculateNextPage(total / size * size);
    }

    private long calculatePageSize(final long start) {
        if (start >= total) {
            return 0;
        }

        if (start + size >= total) {
            return total - start;
        }

        return  size;
    }

    private Paging calculateNextPage(final long start) {
        return toBuilder()
                .start(start)
                .count(calculatePageSize(start))
                .build();
    }
}
