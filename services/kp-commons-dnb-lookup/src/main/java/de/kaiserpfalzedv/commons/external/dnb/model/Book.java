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
    String ean;
    @Builder.Default
    List<String> isbns = new ArrayList<>();
    @Size(max = 1024)
    String title;
    @Size(max = 1024)
    String remainderOfTitle;

    @Builder.Default
    List<String> authors = new ArrayList<>();

    String publisher;
    String placeOfPublication;
    LocalDate dateOfPublication;

    @Size(max = 1024)
    String physicalDescription;
    String termsOfAvailability;
    String series;
    String edition;
    String formOfProduct;
    @Builder.Default
    List<String> bibliographyNumbers = new ArrayList<>();
    @Builder.Default
    List<String> formKeywords = new ArrayList<>();
    @Builder.Default
    Set<String> deweyDecimalClassifications = new HashSet<>();
    Boolean containedInInventory;
    String source;
    @Builder.Default
    Map<String, String> inventoryUris = new HashMap<>();
}
