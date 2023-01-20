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

package de.kaiserpfalzedv.commons.core.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DispatchmentFile contains all data to a single dispatchment
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@Schema(
        description = "Paging data including calculating next/previous and first/last pages available.",
        example = "{\n" +
                "  \"start\": 0, \n" +
                "  \"size\": 100, \n" +
                "  \"count\": 83, \n" +
                "  \"total\": 83,\n" +
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
public class Paging implements de.kaiserpfalzedv.commons.api.resources.Paging {
    @Builder.Default
    private final long start = 0;

    @Builder.Default
    private final long size = 20;

    private final long count;

    private final long total;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public de.kaiserpfalzedv.commons.api.resources.Paging firstPage() {
        return toBuilder()
                .start(0)
                .count(calculatePageSize(0))
                .build();
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public de.kaiserpfalzedv.commons.api.resources.Paging previousPage() {
        if (start - size <= 0) {
            return firstPage();
        }

        return toBuilder()
                .start(start - size)
                .count(calculatePageSize(start - size))
                .build();
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public de.kaiserpfalzedv.commons.api.resources.Paging nextPage() {
        return calculateNextPage(start + size);
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public de.kaiserpfalzedv.commons.api.resources.Paging lastPage() {
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

    private de.kaiserpfalzedv.commons.api.resources.Paging calculateNextPage(final long start) {
        return toBuilder()
                .start(start)
                .count(calculatePageSize(start))
                .build();
    }
}
