/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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
package de.kaiserpfalzedv.commons.r2dbc.liquibase;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

/**
 * <p>ChangeLog -- The liquibase changelog.</p>
 *
 * <p>This is the interface description to the changelog table as described in the
 * <a href="https://docs.liquibase.com/concepts/tracking-tables/databasechangelog-table.html">liquibase
 * documentation</a>.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.2.0
 * @since 2025-06-08
 */
@Schema(
    name = "Liquibase Changelog",
    description = "This is the interface description to the changelog table as described in the liquibase documentation"
)
@Table("DATABASECHANGELOG")
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@EqualsAndHashCode(of = {"id"})
public class ChangeLog {
  /**
   * Value from the changeset id attribute.
   */
  @Schema(
      title = "Id",
      description = "Value from the changeset id attribute.",
      required = true,
      maxLength = 255
  )
  @NotNull
  @Size(max = 255)
  @Id
  @Column("ID")
  @ToString.Include
  private String id;
  
  /**
   * Value from the changeset author attribute.
   */
  @Schema(
      title = "Author",
      description = "Value from the changeset author attribute.",
      required = true,
      maxLength = 255
  )
  @Size(max = 255)
  @Column("AUTHOR")
  private String author;
  
  /**
   * Path to the changelog. This may be an absolute path or a relative path depending on how the changelog was passed
   * to Liquibase. For best results, it should be a relative path. The logicalFilePath attribute can be used on the
   * changelog or on individual changesets.
   */
  @Schema(
      title = "Path to the changelog",
      description = "Path to the changelog. This may be an absolute path or a relative path depending on how " +
          "the changelog was passed to Liquibase. For best results, it should be a relative path. The " +
          "logicalFilePath attribute can be used on the changelog or on individual changesets.",
      required = true,
      maxLength = 255
  )    @Size(max = 255)
  @NotNull
  @Column("FILENAME")
  @ToString.Include
  private String filename;
  
  /**
   * Tracks which changeset correspond to tag operations.
   */
  @Schema(
      title = "Tag",
      description = "Tracks which changeset correspond to tag operations.",
      maxLength = 255
  )
  @Size(max = 255)
  @Column("TAG")
  @ToString.Include
  private String tag;
  
  /**
   * Label(s) used to execute the changeset.
   */
  @Schema(
      title = "Label(s)",
      description = "Label(s) used to execute the changeset.",
      maxLength = 255
  )
  @Size(max = 255)
  @Column("LABELS")
  @ToString.Include
  private String labels;
  
  /**
   * changesets deployed together will have the same unique identifier.
   */
  @Schema(
      title = "Deployment Id",
      description = "changesets deployed together will have the same unique identifier.",
      required = true,
      maxLength = 10
  )
  @NotNull
  @Size(max = 10)
  @Column("DEPLOYMENT_ID")
  @ToString.Include
  private String deploymentId;
  
  /**
   * <p>Order that the changesets were executed. Used in addition to {@link #getExecutionDate()} to ensure order
   * is correct even when the databases datetime supports poor resolution.</p>
   *
   * <p><b>NOTE:</b> The values are only guaranteed to be increasing within an individual update run. There are times
   * where they will restart at zero.</p>
   */
  @Schema(
      title = "Execution Order",
      description = "Order that the changesets were executed. Used in addition to the Execution Date to " +
          "ensure order is correct even when the databases datetime supports poor resolution. NOTE: The " +
          "values are only guaranteed to be increasing within an individual update run. There are times " +
          "where they will restart at zero.",
      required = true,
      minimum = "0"
  )
  @Column("ORDEREXECUTED")
  @ToString.Include
  private int executionOrder;
  
  /**
   * Description of how the changeset was executed.
   */
  @NotNull
  @Column("EXECTYPE")
  private ExecType executionType;
  
  /**
   * Date/time of when the changeset was executed. Used with {@link #getExecutionOrder()} to determine
   * rollback order.
   */
  @Schema(
      title = "Execution Date",
      description = "Date/time of when the changeset was executed. Used with {@link #getExecutionOrder()} to " +
          "determine rollback order.",
      required = true,
      pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}(Z|(+|-)\\d{2}:\\d{2})$",
      maxLength = 255
  )
  @NotNull
  @Size(max = 255)
  @Column("DATEEXECUTED")
  private OffsetDateTime executionDate;
  
  /**
   * Version of Liquibase used to execute the changeset.
   */
  @Schema(
      title = "Liquibase Version",
      description = "Version of Liquibase used to execute the changeset.",
      required = true,
      maxLength = 255
  )
  @NotNull
  @Size(max = 20)
  @Column("LIQUIBASE")
  private String liquibaseVersion;
  
  /**
   * Context(s) used to execute the changeset.
   */
  @Schema(
      title = "Context(s)",
      description = "Context(s) used to execute the changeset.",
      maxLength = 255
  )
  @Size(max = 255)
  @Column("CONTEXTS")
  private String contexts;
  
  /**
   * Checksum of the changeset when it was executed. Used on each run to ensure there have been no unexpected changes
   * to changesets in the changelog file.
   */
  @Schema(
      title = "MD5 Sum",
      description = "Checksum of the changeset when it was executed. Used on each run to ensure there have " +
          "been no unexpected changes to changesets in the changelog file.",
      required = true,
      minLength = 32,
      maxLength = 35
  )
  @NotNull
  @Size(max = 35)
  @Column("MD5SUM")
  private String md5Sum;
  
  /**
   * Short auto-generated human-readable description of changeset
   */
  @Schema(
      title = "Description",
      description = "Short auto-generated human-readable description of changeset.",
      required = true,
      minLength = 1,
      maxLength = 255
  )
  @NotNull
  @Size(max = 255)
  @Column("DESCRIPTION")
  private String description;
  
  /**
   * Value from the changeset comment attribute.
   */
  @Schema(
      title = "Comments",
      description = "Value from the changeset comment attribute.",
      required = true,
      maxLength = 255
  )
  @Size(max = 255)
  @Column("COMMENTS")
  private String comments;
  
  
  /**
   * The liquibase execution type.
   *
   * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
   * @since 3.0.0  2023-01-19
   */
  @Schema(
      title = "Execution Type",
      description = "Description of how the changeset was executed.",
      required = true,
      enumeration = {"EXECUTED","FAILED","SKIPPED","RERAN","MARK_RAN"}
  )
  public enum ExecType {
    EXECUTED, FAILED, SKIPPED, RERAN, MARK_RAN
  }
}
