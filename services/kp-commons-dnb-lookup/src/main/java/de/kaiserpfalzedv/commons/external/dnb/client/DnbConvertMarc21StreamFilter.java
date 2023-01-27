/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.external.dnb.client;

import de.kaiserpfalzedv.commons.external.dnb.marcxml.MarcConverter;
import de.kaiserpfalzedv.commons.external.dnb.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>DnbConvertMarc21StreamFilter -- Converts the MARC21-xml into a {@link List}&lt;{@link Book}&gt; json list.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class DnbConvertMarc21StreamFilter implements ClientResponseFilter {

    private final MarcConverter converter;

    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
        InputStream orig = response.getEntityStream();

        List<Book> books = converter.convert(orig);
        log.debug("Books retrieved. count={}", books.size());

        response.getHeaders().replace("Content-Type", List.of(MediaType.APPLICATION_JSON));
        try (InputStream is = converter.convert(books)) {
            response.setEntityStream(is);
        }
    }
}
