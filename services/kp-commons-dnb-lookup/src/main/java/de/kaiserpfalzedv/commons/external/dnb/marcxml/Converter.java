/*
 * MIT License
 *
 * Copyright (c) 2019 Technische Informationsbibliothek (TIB)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * DISCLAIMER: The software has been adapted to the structure and toolset of kp-commons by Roland Lichti. The original
 * software can be found on <https://github.com/TIBHannover/library-profile-service>.
 */
package de.kaiserpfalzedv.commons.external.dnb.marcxml;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Technische Informationsbibliothek (TIB)
 * @since 1.0.0  2023-01-22
 */
@Slf4j
public abstract class Converter {

    /**
     * Remove attached text from isbns.
     * @param rawIsbns isbns that may include text
     * @return isbn without trailing text
     */
    protected List<String> cleanupIsbns(final Collection<String> rawIsbns) {
        Set<String> isbns = new HashSet<>();
        Pattern pattern = Pattern.compile("([0-9X]+).*");
        for (String isbn : rawIsbns) {
            Matcher matcher = pattern.matcher(isbn.replaceAll("-", ""));
            if (matcher.matches()) {
                isbns.add(matcher.group(1));
            } else {
                isbns.add(isbn);
            }
        }
        return new ArrayList<>(isbns);
    }

}