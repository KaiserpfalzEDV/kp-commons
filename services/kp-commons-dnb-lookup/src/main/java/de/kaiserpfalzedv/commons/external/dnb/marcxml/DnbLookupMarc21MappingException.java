package de.kaiserpfalzedv.commons.external.dnb.marcxml;

import de.kaiserpfalzedv.commons.external.dnb.model.DnbLookupException;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>DnbLookupMarc21MappingException -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Slf4j
public class DnbLookupMarc21MappingException extends DnbLookupException {
    public DnbLookupMarc21MappingException(Throwable cause) {
        super(500, cause.getMessage());
    }
}
