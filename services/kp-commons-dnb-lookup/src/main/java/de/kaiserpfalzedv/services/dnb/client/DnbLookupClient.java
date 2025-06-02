/*
 * Copyright (c) 2023-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

import de.kaiserpfalzedv.services.dnb.model.Book;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>DnbLookupClient -- The client for accessing the webservice.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
public interface DnbLookupClient {
    /**
     * This is the generic query to the DNB index.
     *
     * <p>Will result in something like:</p>
     * <a href="https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3D9783958672567&recordSchema=MARC21-xml">https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3D9783958672567&recordSchema=MARC21-xml</a>
     *
     * @param query The query to be sent. It has to contain the index followed by a '='. The nicest index is 'WOE'.
     * @return A set of Books.
     */
    List<Book> lookup(@RequestParam("query") String query);
}
