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

package de.kaiserpfalzedv.services.dnb.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * <p>Book -- The data set returned by www.ean-search.org</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
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
