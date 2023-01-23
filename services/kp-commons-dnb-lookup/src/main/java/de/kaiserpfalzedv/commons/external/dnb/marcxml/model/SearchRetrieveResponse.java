package de.kaiserpfalzedv.commons.external.dnb.marcxml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * <p>SearchRetrieveResponse -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-23
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@XmlRootElement(name = "searchRetrieveResponse", namespace = "http://www.loc.gov/zing/srw/")
public class SearchRetrieveResponse {
    private String version;
    private int numberOfRecords;

    List<Record> records;

    SearchRetrieveRequest echoedSearchRetrieveRequest;

    @Jacksonized
    @SuperBuilder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @XmlType(name = "record", namespace = "http://www.loc.gov/zing/srw/")
    public static class Record {
        private String recordSchema;
        private String recordPacking;

        private String recordData;

        private int recordPosition;
    }

    @Jacksonized
    @SuperBuilder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @XmlType(namespace = "http://www.loc.gov/zing/srw/")
    public static class SearchRetrieveRequest {
        private String version;
        private String query;
        private String recordSchema;
        private XQuery xQuery;
    }

    @Jacksonized
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @XmlType(namespace = "http://www.w3.org/2001/XMLSchema-instance")
    public static class XQuery {
    }
}
