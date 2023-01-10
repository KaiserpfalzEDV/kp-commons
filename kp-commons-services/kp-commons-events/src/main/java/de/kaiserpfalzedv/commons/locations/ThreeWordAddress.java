/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.locations;

import javax.validation.constraints.Pattern;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * ThreeWordAddress -- A 3word address according to <a href="https://what3words.com/">https://what3words.com/</a>.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.1.0  2021-06-13
 */
@Jacksonized
@Builder(toBuilder = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Schema(description = "A 3word address (see https://what3words.com/)")
public class ThreeWordAddress {
    @Pattern(
            regexp = "(///)?\\w+.\\w+.\\w+",
            message= "A 3WordAddress needs to start with '///' and must consist of 3 words seperated by '.' (pattern='///\\w+.\\w+.\\w+')"
    )
    @Schema(description = "The three words.", required = true, minLength=5, example = "///geteilt.flexibler.entfernt")
    private final String address;

    public String getAddress() {
        return address.startsWith("///") ? address : String.format("///%s", address);
    }

    public String toString() {
        return getAddress();
    }
}
