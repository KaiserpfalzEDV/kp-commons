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

package de.kaiserpfalzedv.services.dnb.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.kaiserpfalzedv.services.dnb.model.Book;

/**
 * <p>DnbLookupClient -- The client for accessing the webservice.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@FeignClient(name = "dnb-lookup", configuration = DnbLookupClientConfig.class)
public interface DnbLookupClient {
    /**
     * This is the generic query to the DNB index.
     *
     * <p>Will result in something like:</p>
     * {@literal <pre>https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3D9783958672567&recordSchema=MARC21-xml</pre>}
     *
     * @param query The query to be sent. It has to contain the index followed by a '='. The nicest index is 'WOE'.
     * @return A set of Books.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sru/dnb", produces = "text/xml", consumes = "text/xml")
    List<Book> lookup(@RequestParam("query") String query);

    /*
    @GetMapping(value = "/sru/dnb", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Book> lookup(final String query) {
        final Flux<Book> books = WebClient.create("https://services.dnb.de/")
                .get()
                .retrieve()
                .bodyToFlux(Book.class);

        return books;
    }
 */
}
