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
import org.marc4j.MarcException;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.enterprise.context.Dependent;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public class MarcXmlConverter extends Converter {

    public enum ConversionError {
        XPATH_EVALUATION_FAILED,
        NODE_TRANSFORMATION_FAILED,
        IO_EXCEPTION,
        MARCXML_PARSING_FAILED
    }

    private final List<ConversionError> errors = new ArrayList<>();


    /**
     * Extract all marcxml records from the xml-document with the given xpathExpression and convert
     * the records into {@link Book}s.
     * <p>
     * Errors that occur during conversion will be collected
     * </p>
     *
     * @param xpathExpression the xpathExpression of the marcxml-records
     * @param xmlInput xml-document as {@link String}
     * @return converted records; null, if the given xml-document cannot be parsed
     */
    public List<Book> extractMarcXmlRecordsAndConvert(final String xpathExpression,
                                                                  final String xmlInput) {
        try (ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(xmlInput.getBytes(StandardCharsets.UTF_8))) {
            return extractMarcXmlRecordsAndConvert(xpathExpression, xmlInputStream);
        } catch (IOException e) {
            log.warn("Error closing ByteArrayInputStream", e);
            addConversionError(ConversionError.IO_EXCEPTION);
        }
        return null;
    }

    /**
     * Extract all marcxml records from the xml-document with the given xpathExpression and convert
     * the records into {@link de.kaiserpfalzedv.commons.external.dnb.model.Book}s.
     * <p>
     * Errors that occur during conversion will be collected
     * </p>
     *
     * @param xpathExpression the xpathExpression of the marcxml-records
     * @param xmlInputStream xml-document as {@link InputStream}
     * @return converted records; null, if the given xml-document cannot be parsed
     */
    public List<Book> extractMarcXmlRecordsAndConvert(final String xpathExpression,
                                                      final InputStream xmlInputStream) {
        final NodeList nodes;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document xml = builder.parse(xmlInputStream);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            nodes = (NodeList) xpath.compile(xpathExpression).evaluate(xml, XPathConstants.NODESET);
        } catch (IOException | ParserConfigurationException | XPathExpressionException
                 | SAXException e) {
            log.error("Cannot evaluate xml document with xpathExpression: " + xpathExpression, e);
            addConversionError(ConversionError.XPATH_EVALUATION_FAILED);
            return null;
        }

        final List<Book> documents = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                final Transformer xform = factory.newTransformer();
                xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                xform.setOutputProperty(OutputKeys.INDENT, "yes");
                xform.transform(new DOMSource(node), new StreamResult(outputStream));
                try (InputStream recordInputStream = new ByteArrayInputStream(outputStream
                        .toByteArray())) {
                    documents.addAll(convertMarcXmlRecords(recordInputStream));
                }
            } catch (IOException | TransformerException e) {
                log.error("Error while transforming node", e);
                addConversionError(ConversionError.NODE_TRANSFORMATION_FAILED);
            }
        }
        return documents;
    }

    /**
     * Call {@link #convertMarcXmlRecords(InputStream)}.
     *
     * @param records marcxml records as {@link String}
     * @return converted records
     */
    public List<Book> convertMarcXmlRecords(final String records) {
        try (ByteArrayInputStream recordInputStream = new ByteArrayInputStream(records.getBytes(StandardCharsets.UTF_8))) {
            return convertMarcXmlRecords(recordInputStream);
        } catch (IOException e) {
            log.warn("Error closing ByteArrayInputStream", e);
            addConversionError(ConversionError.IO_EXCEPTION);
        }
        return null;
    }

    /**
     * Convert all marcxml records into {@link Book}s.
     * <p>
     * Errors that occur during conversion will be collected.
     * </p>
     *
     * @param recordsInputStream marcxml records as xml-document as {@link InputStream}
     * @return converted records
     */
    public List<Book> convertMarcXmlRecords(final InputStream recordsInputStream) {
        final List<Book> documents = new ArrayList<>();
        try {
            final MarcXmlReader reader = new MarcXmlReader(recordsInputStream);
            while (reader.hasNext()) {
                final Record record = reader.next();
                documents.add(record2Document(record));
            }
        } catch (MarcException e) {
            log.warn("Error while parsing marcxml", e);
            addConversionError(ConversionError.MARCXML_PARSING_FAILED);
        }
        return documents;
    }

    /**
     * Convert the record into a {@link Book}.
     * @param record record to convert
     * @return document
     */
    protected Book record2Document(final Record record) {
        return Book.builder()

        .title(getDataIfExists(record, "245", 'a'))
        .remainderOfTitle(getDataIfExists(record, "245", 'b'))
        .isbns(getIsbns(record))
        .publisher(getPublisher(record))
        .placeOfPublication(getPublicationPlace(record))
        .dateOfPublication(getPublicationDate(record))
        .edition(getDataIfExists(record, "250", 'a'))
        .physicalDescription(getPhysicalDescription(record))
        .series(getSeries(record))
        .formOfProduct(getFormOfProduct(record))
        .termsOfAvailability(getDataIfExists(record, "020", 'c'))
        .authors(getAuthors(record))
        .deweyDecimalClassifications(getDeweyDecimalClassifications(record))
        .formKeywords(getAllData(record, "655", 'a'))
        .bibliographyNumbers(getAllData(record, "015", 'a'))
                .build();
    }

    private List<String> getIsbns(final Record record) {
        return cleanupIsbns(getAllData(record, "020", 'a'));
    }

    private String getFormOfProduct(final Record record) {
        StringBuilder form = new StringBuilder();
        for (VariableField field : record.find("653", "\\(Produktform\\).*")) {
            if (form.length() > 0) {
                form.append(", ");
            }
            form.append(getData(field, 'a').substring(13));
        }
        return form.toString();
    }

    private Set<String> getDeweyDecimalClassifications(final Record record) {
        final Set<String> deweyDecimalClassifications = new HashSet<>();
        for (VariableField field : record.getVariableFields("082")) {
            if (field instanceof DataField) {
                List<String> classificationNumbers = ((DataField) field).getSubfields('a').stream()
                        .map(Subfield::getData).toList();
                deweyDecimalClassifications.addAll(classificationNumbers);

            }
        }
        return deweyDecimalClassifications;
    }

    private List<String> getAuthors(final Record record) {
        Set<String> authors = new HashSet<>();
        String fieldMainEntryPersonalName = getDataIfExists(record, "100", 'a');
        if (fieldMainEntryPersonalName != null) {
            addAuthors(authors, fieldMainEntryPersonalName);
        }

        getAllData(record, "700", 'a', "aut").forEach(a -> addAuthors(authors, a));
        return new ArrayList<>(authors);
    }

    /**
     * Add all authors from the given string. Authors may be separated by semicolon here.
     * @param authors add authors here
     * @param authorsString parse authors from here
     */
    private void addAuthors(final Set<String> authors, final String authorsString) {
        for (String author : authorsString.split(";")) {
            authors.add(author.trim());
        }
    }

    private String getSeries(final Record record) {
        StringBuilder series = new StringBuilder();
        for (VariableField field : record.getVariableFields("490")) {
            series.append(getData(field, 'a'));
            String volume = getData(field, 'v');
            if (volume.length() > 0) {
                series.append(", ").append(volume);
            }
        }
        return series.toString();
    }

    private String getPhysicalDescription(final Record record) {
        StringBuilder physicalDesc = new StringBuilder();
        for (VariableField field : record.getVariableFields("300")) {
            String extent = getData(field, 'a');
            physicalDesc.append(extent);
            String otherDetails = getData(field, 'b');
            if (otherDetails.length() > 0 && !extent.endsWith(":")) {
                physicalDesc.append(", ");
            }
            physicalDesc.append(otherDetails);
            String dimensions = getData(field, 'c');
            if (dimensions.length() > 0) {
                physicalDesc.append(", ");
                physicalDesc.append(dimensions);
            }
            String accompanyingMaterial = getData(field, 'e');
            if (accompanyingMaterial.length() > 0) {
                physicalDesc.append(", ");
                physicalDesc.append(accompanyingMaterial);
            }
        }
        return physicalDesc.toString();
    }

    private String getData(final VariableField field, final char code) {
        if (field instanceof DataField && ((DataField) field).getSubfield(code) != null) {
            String data = ((DataField) field).getSubfield(code).getData().trim();
            data = Normalizer.normalize(data, Form.NFC);
            data = data.replaceAll("\u0098", "");
            data = data.replaceAll("\u009C", "");
            return data;
        }
        return "";
    }

    private String getPublicationPlace(final Record record) {
        return getPublicationInfo(record, 'a');
    }

    private String getPublisher(final Record record) {
        return getPublicationInfo(record, 'b');
    }

    private LocalDate getPublicationDate(final Record record) {
        String regex = getPublicationInfo(record, 'c').contains(".") ? "." : "-";

        String[] data = getPublicationInfo(record, 'c').split(regex);

        return switch (data.length) {
            case 1 -> convertDate(data[0], "1", "1", regex);

            case 2 -> convertDate(data[0], data[1], data[0], regex);

            case 3 -> convertDate(data[0], data[1], data[2], regex);

            default -> LocalDate.EPOCH;
        };
    }

    private static LocalDate convertDate(final String a, final String b, final String c, final String regex) {
        return "-".equals(regex)
                ? LocalDate.of(
                        Integer.parseInt(a, 10),
                        Integer.parseInt(b, 10),
                        Integer.parseInt(c, 10)
                  )
                : LocalDate.of(
                        Integer.parseInt(c, 10),
                        Integer.parseInt(b, 10),
                        Integer.parseInt(a, 10)
                  );
    }

    /**
     * Get publication info from the given record.
     * <ul>
     * <li>code = <i>a</i> => place</li>
     * <li>code = <i>b</i> => publisher name</li>
     * <li>code = <i>c</i> => data</li>
     * </ul>
     *
     * @param record the record
     * @param code the code
     * @return the string containing the information.
     */
    private String getPublicationInfo(final Record record, final char code) {
        String publicationInfo = null;
        List<String> publicationInfos = getAllData(record, "264", code, null, null, '1');
        if (publicationInfos.size() > 0) {
            publicationInfo = publicationInfos.get(0).trim(); // use first entry, ignore others
        }
        if (publicationInfo == null) {
            publicationInfos = getAllData(record, "260", code);
            if (publicationInfos.size() > 0) {
                publicationInfo = publicationInfos.get(0).trim(); // use first entry, ignore others
            }
        }
        return publicationInfo;
    }

    private List<String> getAllData(final Record record, final String tag, final char code) {
        return getAllData(record, tag, code, null);
    }

    private List<String> getAllData(final Record record, final String tag, final char code,
                                    final String pattern) {
        return getAllData(record, tag, code, pattern, null, null);
    }

    /**
     * Determine data matching the given criteria.
     *
     * @param record record to extract the data from
     * @param tag tag of the data field
     * @param code code of the subfield
     * @param pattern subfield-data has to match this pattern; may be null(no pattern-check)
     * @param indicator1 indicator1 of the data field; may be null - in this case: ignore indicator1
     * @param indicator2 indicator2 of the data field; may be null - in this case: ignore indicator2
     * @return a list containing all data fields.
     */
    protected List<String> getAllData(final Record record,
                                      final String tag,
                                      final char code,
                                      final String pattern,
                                      @SuppressWarnings("SameParameterValue") final Character indicator1,
                                      final Character indicator2) {
        List<VariableField> fields;
        if (pattern != null) {
            fields = record.find(tag, pattern);
        } else {
            fields = record.getVariableFields(tag);
        }
        return fields.stream()
                .filter(f -> f instanceof DataField && ((DataField) f).getSubfield(code) != null
                        && (indicator1 == null || ((DataField) f).getIndicator1() == indicator1)
                        && (indicator2 == null || ((DataField) f).getIndicator2() == indicator2))
                .map(f -> getData(f, code))
                .collect(Collectors.toList());
    }

    private String getDataIfExists(final Record record, final String tag, final char code) {
        List<String> data = getAllData(record, tag, code);
        if (data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    private void addConversionError(final ConversionError conversionError) {
        this.errors.add(conversionError);
    }

    /**
     * Get all errors that occurred during conversion.
     *
     * @return the errors that occurred during conversion
     */
    public List<ConversionError> getErrors() {
        return errors;
    }

    /**
     * Check, if there was an error during conversion.
     *
     * @return true, if there was an error during conversion; false, otherwise
     */
    public boolean hasErrors() {
        return errors.size() > 0;
    }

}
