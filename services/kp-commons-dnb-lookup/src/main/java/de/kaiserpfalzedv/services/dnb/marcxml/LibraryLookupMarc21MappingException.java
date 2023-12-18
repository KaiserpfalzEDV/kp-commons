package de.kaiserpfalzedv.services.dnb.marcxml;

import de.kaiserpfalzedv.services.dnb.model.LibraryLookupException;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>LibraryLookupMarc21MappingException -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Slf4j
public class LibraryLookupMarc21MappingException extends LibraryLookupException {
    public LibraryLookupMarc21MappingException(Throwable cause) {
        super(500, cause.getMessage());
    }
}
