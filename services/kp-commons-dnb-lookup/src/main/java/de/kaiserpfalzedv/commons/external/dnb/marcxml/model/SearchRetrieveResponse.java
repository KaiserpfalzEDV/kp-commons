package de.kaiserpfalzedv.commons.external.dnb.marcxml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.xml.bind.annotation.*;
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
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@XmlRootElement(name = "searchRetrieveResponse", namespace = "http://www.loc.gov/zing/srw/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchRetrieveResponse {
    @XmlElement
    private String version;
    @XmlElement
    private int numberOfRecords;

    @XmlElementWrapper(name = "records")
    @XmlElement(name = "record")
    List<Record> records;

    @XmlElement
    SearchRetrieveRequest echoedSearchRetrieveRequest;
}
