package de.kaiserpfalzedv.services.dnb.marcxml.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * <p>Marc21Record -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-27
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
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
public class Marc21Record {
    @XmlAttribute
    private String type;
    @XmlElement
    private String leader;
    @JacksonXmlProperty(localName = "controlfield")
    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "controlfield")
    private List<ControlField> controlfields;

    @JacksonXmlProperty(localName = "datafield")
    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "datafield")
    private List<DataField> datafields;
}
