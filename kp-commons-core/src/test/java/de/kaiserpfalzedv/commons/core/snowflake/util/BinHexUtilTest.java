/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.snowflake.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class BinHexUtilTest {
	@Test
	public void testBin() {
		log.debug(BinHexUtil.bin(-1L));
		log.debug(BinHexUtil.bin(-1L << 12));
		log.debug(BinHexUtil.bin(~(-1L << 12)));

		assertEquals("1111111111111111111111111111111111111111111111111111111111111111", BinHexUtil.bin(-1L));
		assertEquals("1111111111111111111111111111111111111111111111111111000000000000",
				BinHexUtil.bin(-1L << 12));
		assertEquals("0000000000000000000000000000000000000000000000000000111111111111",
				BinHexUtil.bin(~(-1L << 12)));
	}

	@Test
	public void testHex() {
		log.debug(BinHexUtil.hex(-1L));
		log.debug(BinHexUtil.hex(-1L << 12));
		log.debug(BinHexUtil.hex(~(-1L << 12)));

		assertEquals("FFFFFFFFFFFFFFFF", BinHexUtil.hex(-1L));
		assertEquals("FFFFFFFFFFFFF000", BinHexUtil.hex(-1L << 12));
		assertEquals("0000000000000FFF", BinHexUtil.hex(~(-1L << 12)));
	}

	@Test
	public void testDiode() {
		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(1, 41)));
		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(1 + 41, 5)));
		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(1 + 41 + 5, 5)));
		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(1 + 41 + 5 + 5, 12)));


		assertEquals("0111111111111111111111111111111111111111110000000000000000000000",
				BinHexUtil.bin(BinHexUtil.diode(1, 41)));

		assertEquals("0000000000000000000000000000000000000000001111100000000000000000",
				BinHexUtil.bin(BinHexUtil.diode(1 + 41, 5)));

		assertEquals("0000000000000000000000000000000000000000000000011111000000000000",
				BinHexUtil.bin(BinHexUtil.diode(1 + 41 + 5, 5)));

		assertEquals("0000000000000000000000000000000000000000000000000000111111111111",
				BinHexUtil.bin(BinHexUtil.diode(1 + 41 + 5 + 5, 12)));

		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(0, 0)));
		log.debug("diode: {}", BinHexUtil.bin(BinHexUtil.diode(0, 64)));

		assertEquals("0000000000000000000000000000000000000000000000000000000000000000",
				BinHexUtil.bin(BinHexUtil.diode(0, 0)));

		assertEquals("1111111111111111111111111111111111111111111111111111111111111111",
				BinHexUtil.bin(BinHexUtil.diode(0, 64)));

	}
}
