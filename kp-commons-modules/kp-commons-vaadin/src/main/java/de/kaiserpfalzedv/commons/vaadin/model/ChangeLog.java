/*
 * Copyright (c) 2023 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.vaadin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

/**
 * ChangeLog --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChangeLog implements de.kaiserpfalzedv.commons.api.liquibase.model.ChangeLog {
    private String id;
    private String author;
    private String filename;
    private OffsetDateTime executionDate;
    private int executionOrder;
    private ExecType executionType;
    private String md5Sum;
    private String description;
    private String comments;
    private String tag;
    private String liquibaseVersion;
    private String contexts;
    private String labels;
    private String deploymentId;
}
