package de.kaiserpfalzedv.services.dnb.marcxml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import jakarta.xml.bind.annotation.*;

/**
 * <p>ControlField -- .</p>
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
public class ControlField {
    @ToString.Include
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String tag;

    @JacksonXmlText
    @XmlValue
    private String content;
}
