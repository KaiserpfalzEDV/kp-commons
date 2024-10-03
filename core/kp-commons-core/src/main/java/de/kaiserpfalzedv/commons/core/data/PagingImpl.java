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

package de.kaiserpfalzedv.commons.core.data;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.kaiserpfalzedv.commons.api.resources.Paging;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * DispatchmentFile contains all data to a single dispatchment
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@Schema(
        description = "Paging data including calculating next/previous and first/last pages available.",
        title = "Pagination data for transfer lists"
)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PagingImpl implements Paging {
    private static final long serialVersionUID = 0L;

    @Builder.Default
    private final long start = 0;

    @Builder.Default
    private final long size = 20;

    private final long count;

    private final long total;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Paging firstPage() {
        return this.toBuilder()
                .start(0)
                .count(this.calculatePageSize(0))
                .build();
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Paging previousPage() {
        if (this.start - this.size <= 0) {
            return this.firstPage();
        }

        return this.toBuilder()
                .start(this.start - this.size)
                .count(this.calculatePageSize(this.start - this.size))
                .build();
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Paging nextPage() {
        return this.calculateNextPage(this.start + this.size);
    }

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Paging lastPage() {
        return this.calculateNextPage(this.total / this.size * this.size);
    }

    private long calculatePageSize(final long start) {
        if (start >= this.total) {
            return 0;
        }

        if (start + this.size >= this.total) {
            return this.total - start;
        }

        return  this.size;
    }

    private Paging calculateNextPage(final long start) {
        return this.toBuilder()
                .start(start)
                .count(this.calculatePageSize(start))
                .build();
    }
}
