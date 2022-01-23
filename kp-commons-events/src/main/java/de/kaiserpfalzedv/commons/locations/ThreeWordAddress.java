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

import jakarta.validation.constraints.NotNull;
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
@Getter
@EqualsAndHashCode
@Schema(description = "A 3word address (see https://what3words.com/)")
public class ThreeWordAddress {
    @Schema(description = "First word.", required = true, example = "geteilt")
    private final String word1;
    @Schema(description = "Second word.", required = true, example = "flexibler")
    private final String word2;
    @Schema(description = "Third word.", required = true, example = "entfernt")
    private final String word3;

    public String toString() {
        return String.format("///%s.%s.%s", word1, word2, word3);
    }

    /**
     * Parses the given 3word address to a ThreeWordAddress object.
     *
     * @param threeWordAddress a 3word address in form "{@code ///<word>.<word>.<word>}".
     * @return The ThreeWordAddress objects.
     * @throws IllegalArgumentException When the address does not conform to the format
     *                                  {@code ///<word>.<word>.<word>}.
     */
    public static ThreeWordAddress parse(@NotNull final String threeWordAddress) {
        if (!threeWordAddress.startsWith("///")) {
            throw new IllegalArgumentException(String.format(
                    "Only 3word addresses starting with '///' are parseable ('%s' is invalid).",
                    threeWordAddress
            ));
        }

        String[] words = threeWordAddress.substring(3).split("\\.");
        if (words.length != 3) {
            throw new IllegalArgumentException(String.format(
                    "Only 3word addresses containing 3 words are parseable ('%s' is invalid, it contains only %d words).",
                    threeWordAddress,
                    words.length
            ));
        }

        return new ThreeWordAddress(words[0], words[1], words[2]);
    }
}
