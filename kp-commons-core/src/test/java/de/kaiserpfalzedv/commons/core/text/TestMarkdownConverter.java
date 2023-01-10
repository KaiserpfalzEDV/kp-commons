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

package de.kaiserpfalzedv.commons.core.text;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the markdown converter.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Slf4j
public class TestMarkdownConverter extends AbstractTestBase {
    public TestMarkdownConverter() {
        setTestSuite(getClass().getSimpleName());
        setLog(log);
    }

    /**
     * The markdown converter to be tested.
     */
    private final MarkdownConverter sut = new MarkdownConverter();

    @Test
    public void shouldConvertTheMarkdownCorrectWhenTextIsGiven() {
        startTest("convert-markdown");

        String input = "### MD-Test\n\nDas hier ist der Test.";

        String result = sut.convert(input);

        assertEquals("<h3>MD-Test</h3>\n<p>Das hier ist der Test.</p>\n", result);
    }
}
