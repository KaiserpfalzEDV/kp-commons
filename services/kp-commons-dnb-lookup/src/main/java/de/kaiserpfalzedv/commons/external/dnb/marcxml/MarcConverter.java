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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.kaiserpfalzedv.commons.external.dnb.marcxml.model.DataField;
import de.kaiserpfalzedv.commons.external.dnb.marcxml.model.Record;
import de.kaiserpfalzedv.commons.external.dnb.marcxml.model.SearchRetrieveResponse;
import de.kaiserpfalzedv.commons.external.dnb.marcxml.model.SubField;
import de.kaiserpfalzedv.commons.external.dnb.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>MarcXmlConverter -- .</p>
 *
 * @author Technische Informationsbibliothek (TIB) Hannover
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Dependent
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@SuppressWarnings("unused")
@Slf4j
public class MarcConverter {
    private final XmlMapper mapper = new XmlMapper();
    private final ObjectMapper jsonMapper;

    public List<Book> convert(SearchRetrieveResponse response) {
        log.info("Converting response. query='{}', count={}",
                response.getEchoedSearchRetrieveRequest().getQuery(), response.getNumberOfRecords());

        return response.getRecords().stream()
                .map(this::mapRecordToBook)
                .toList();
    }

    private Book mapRecordToBook(Record record) {
        final Book.BookBuilder result = Book.builder();

        log.debug("Converting record. position={}, schema='{}', packing='{}', datafields={}",
                record.getRecordPosition(), record.getRecordSchema(), record.getRecordPacking(),
                record.getRecordData().getRecord().getDatafields());

        List<DataField> data = record.getRecordData().getRecord().getDatafields();

        final List<String> isbns = new ArrayList<>();
        final List<String> authors = new ArrayList<>();
        for (DataField d : data) {
            log.trace("Working on data field. tag='{}', ind1='{}', ind2='{}' subfields={}",
                    d.getTag(), d.getInd1(), d.getInd2(), d.getSubfield());

            switch(d.getTag()) {
                case "024":
                    result.ean(getSubField(d, "a"));
                    break;

                case "020":
                    isbns.add(getSubField(d, "9"));
                    break;

                case "245":
                    result.title(getSubField(d, "a"));
                    result.subTitle(getSubField(d, "b"));
                    break;

                case "490":
                    result.series(getSubField(d, "a"));
                    break;

                case "100":
                case "700":
                    authors.add(getSubField(d, "a"));
                    break;

                case "710":
                    result.publisher(getSubField(d, "a"));
                    break;

                case "264":
                    result.publisher(getSubField(d, "b"));
                    result.placeOfPublication(getSubField(d, "a"));
                    break;

                default:
                    continue;
            }
        }

        result.authors(authors);
        result.isbns(isbns);

        return result.build();
    }

    private String getSubField(DataField data, @NotBlank final String code) {
        String result = data.getSubfield().stream()
                .map(f -> retrieveSubField(f, code))
                .filter(s -> !s.isBlank())
                .toList().get(0);

        if (! result.isBlank()) {
            return result;
        }

        return "";
    }

    private String retrieveSubField(final SubField field, final String code) {
        if (code.equals(field.getCode())) {
            return field.getContent();
        }

        return "";
    }


    public List<Book> convert(InputStream is) {
        try {
            return convert(mapper.readValue(is, SearchRetrieveResponse.class));
        } catch (IOException e) {
            throw new DnbLookupMarc21MappingException(e);
        }
    }

    public InputStream convertXml(List<Book> books) {
        try {
            return IOUtils.toInputStream(mapper.writeValueAsString(books));
        } catch (IOException e) {
            throw new DnbLookupMarc21MappingException(e);
        }
    }

    public InputStream convert(List<Book> books) {
        try {
            return IOUtils.toInputStream(new String(jsonMapper.writeValueAsBytes(books)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DnbLookupMarc21MappingException(e);
        }
    }
}
