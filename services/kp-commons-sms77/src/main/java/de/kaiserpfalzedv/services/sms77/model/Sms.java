/*
 * Copyright (c) 2023-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.sms77.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * <p>Sms -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-21
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
public class Sms implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    
    @ToString.Include
    @Size(max = 11)
    private String from;
    
    @ToString.Include
    private Set<String> to;

    @Size(max = 1520)
    private String text;

    @Builder.Default
    @Min(0) @Max(1)
    private int no_reload = 1;

    @Builder.Default
    @Min(0) @Max(1)
    private int flash = 0;

    @Builder.Default
    private String ttl = "60";
    
    @Builder.Default
    @Min(0) @Max(1)
    private int return_msg_id = 1;
    
    @ToString.Include
    @Builder.Default
    @Size(max = 64)
    private String foreign_id = UUID.randomUUID().toString();

    @Size(max = 100)
    @Pattern(regexp = "^[-._@ a-zA-Z0-9]+$")
    private String label;
}
