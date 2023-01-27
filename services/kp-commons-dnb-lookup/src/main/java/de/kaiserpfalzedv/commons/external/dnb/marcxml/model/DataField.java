package de.kaiserpfalzedv.commons.external.dnb.marcxml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * <p>DataField -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-27
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@XmlType(namespace = "http://www.loc.gov/MARC21/slim")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataField {
    @ToString.Include
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String tag;
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String ind1;
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String ind2;

    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "subfield")
    private List<SubField> subfield;
}
