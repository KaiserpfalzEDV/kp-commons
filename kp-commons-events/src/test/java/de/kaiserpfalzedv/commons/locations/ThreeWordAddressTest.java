/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ThreeWordAddressTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-13
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class ThreeWordAddressTest {

    @Test
    public void shouldWorkWithBuilder() {
        ThreeWordAddress result = ThreeWordAddress.builder()
                .withWord1("geteilt")
                .withWord2("flexibler")
                .withWord3("entfernt")
                .build();

        assertNotNull(result);
        assertEquals("geteilt", result.getWord1());
        assertEquals("flexibler", result.getWord2());
        assertEquals("entfernt", result.getWord3());
        assertEquals(result, result);
        assertEquals(115297568, result.hashCode());
    }

    @Test
    public void shouldReturnAValidThreeWordAddressForKaiserpfalzEDVHeadQuarter() {
        ThreeWordAddress result = ThreeWordAddress.parse("///geteilt.flexibler.entfernt");
        log.trace("result: {}", result);

        assertNotNull(result);
        assertEquals("geteilt", result.getWord1());
        assertEquals("flexibler", result.getWord2());
        assertEquals("entfernt", result.getWord3());
    }

    @Test
    public void shouldThrowAnIllegalArgumentExceptionWithoutLeadingSlashes() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ThreeWordAddress.parse("geteilt.flexibler.entfernt")
        );
    }

    @Test
    public void shouldThrowAnIllegalArgumentExceptionWithNotExactelyThreeWords() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ThreeWordAddress.parse("///geteilt.flexibler")
        );
    }
}
