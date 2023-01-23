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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * DISCLAIMER: The software has been adapted to the structure and toolset of kp-commons by Roland Lichti. The original
 * software can be found on <https://github.com/TIBHannover/library-profile-service>.
 */
package de.kaiserpfalzedv.commons.external.dnb.marcxml;

import de.kaiserpfalzedv.commons.external.dnb.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.xbib.marc.Marc;
import org.xbib.marc.transformer.value.MarcValueTransformers;
import org.xbib.marc.xml.MarcXchangeWriter;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collections;
import java.util.List;

/**
 * <p>MarcXmlConverter -- .</p>
 *
 * @author Technische Informationsbibliothek (TIB) Hannover
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Dependent
@SuppressWarnings("unused")
@Slf4j
public class MarcConverter {
    public List<Book> convertToJson(InputStream is) {
        StringWriter result = new StringWriter();

        MarcValueTransformers transformers = new MarcValueTransformers();
        transformers.setMarcValueTransformer(value -> Normalizer.normalize(value, Normalizer.Form.NFC));

        try (MarcXchangeWriter writer = new MarcXchangeWriter(result)) {
            writer.setMarcValueTransformers(transformers);

            Marc.builder()
                    .setInputStream(is)
                    .setCharset(StandardCharsets.UTF_8)
                    .setMarcListener(writer)
                    .build()
                    .writeCollection();
        } catch (IOException e) {
            throw new DnbLookupMarc21MappingException(e);
        }

        System.out.println(result.toString());

        return Collections.emptyList();
    }
}
