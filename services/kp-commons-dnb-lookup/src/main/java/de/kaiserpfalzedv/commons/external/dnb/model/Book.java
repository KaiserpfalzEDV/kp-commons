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

package de.kaiserpfalzedv.commons.external.dnb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>Book -- The data set returned by www.ean-search.org</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Book {
    @ToString.Include
    private String ean;
    @ToString.Include
    @Builder.Default
    private List<String> isbns = new ArrayList<>();

    @ToString.Include
    @Size(max = 1024)
    private String title;
    @ToString.Include
    @Size(max = 1024)
    private String subTitle;
    @Size(max = 1024)
    private String remainderOfTitle;

    @ToString.Include
    @Builder.Default
    private List<String> authors = new ArrayList<>();

    @ToString.Include
    private String publisher;
    private String placeOfPublication;
    private LocalDate dateOfPublication;

    @Size(max = 1024)
    private String physicalDescription;
    private String termsOfAvailability;
    @ToString.Include
    private String series;
    private String edition;
    private String formOfProduct;
    @Builder.Default
    private List<String> bibliographyNumbers = new ArrayList<>();
    @Builder.Default
    private List<String> formKeywords = new ArrayList<>();
    @Builder.Default
    private Set<String> deweyDecimalClassifications = new HashSet<>();
    private Boolean containedInInventory;
    private String source;
    @Builder.Default
    private Map<String, String> inventoryUris = new HashMap<>();
}
