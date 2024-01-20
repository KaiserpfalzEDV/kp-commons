package de.kaiserpfalzedv.services.dnb.marcxml;

import de.kaiserpfalzedv.services.dnb.model.LibraryLookupException;

/**
 * <p>LibraryLookupMarc21MappingException -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
public class LibraryLookupMarc21MappingException extends LibraryLookupException {
    public LibraryLookupMarc21MappingException(final Throwable cause) {
        super(500, cause.getMessage());
    }
}
