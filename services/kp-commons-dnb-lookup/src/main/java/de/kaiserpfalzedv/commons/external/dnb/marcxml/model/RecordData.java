package de.kaiserpfalzedv.commons.external.dnb.marcxml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>RecordData -- .</p>
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
@XmlType(namespace = "http://www.loc.gov/zing/srw/")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordData {
    @XmlElement
    private Marc21Record record;
}
