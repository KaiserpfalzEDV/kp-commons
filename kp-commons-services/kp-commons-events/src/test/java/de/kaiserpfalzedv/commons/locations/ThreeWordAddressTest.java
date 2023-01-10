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

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ThreeWordAddressTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-13
 */
@Slf4j
public class ThreeWordAddressTest extends AbstractTestBase {

    public ThreeWordAddressTest() {
        super.setTestSuite(getClass().getSimpleName());
        super.setLog(log);
    }

    @Test
    public void shouldWorkWhenAdressStartsWith3Slashes() {
        startTest("with-3-slashes", "///geteilt.flexibler.entfernt");

        ThreeWordAddress result = ThreeWordAddress.builder()
                .address("///geteilt.flexibler.entfernt")
                .build();

        assertNotNull(result);
        assertEquals("///geteilt.flexibler.entfernt", result.getAddress());
        assertEquals(ThreeWordAddress.builder()
                .address("geteilt.flexibler.entfernt")
                .build(), result);
    }

    @Test
    public void shouldWorkWhenAdressStartsWithoutSlashes() {
        startTest("without-3-slashes", "geteilt.flexibler.entfernt");

        ThreeWordAddress result = ThreeWordAddress.builder()
                .address("geteilt.flexibler.entfernt")
                .build();

        assertNotNull(result);
        assertEquals("///geteilt.flexibler.entfernt", result.getAddress());
        assertEquals(ThreeWordAddress.builder()
                .address("///geteilt.flexibler.entfernt")
                .build(), result);
    }
}
