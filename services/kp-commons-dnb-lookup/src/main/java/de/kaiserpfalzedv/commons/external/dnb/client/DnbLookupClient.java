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

import de.kaiserpfalzedv.commons.external.dnb.marcxml.model.SearchRetrieveResponse;
import de.kaiserpfalzedv.commons.external.dnb.model.DnbLookupException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.cache.CacheResult;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.ClientQueryParams;
import io.smallrye.faulttolerance.api.RateLimit;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.TEXT_XML;

/**
 * <p>DnbLookupClient -- The client for accessing the webservice.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@RegisterRestClient
@RegisterProvider(value = ResponseErrorMapper.class, priority = 11)
@RegisterProvider(value = DnbLookupReplaceContentTypeFilter.class, priority = 1000)
@RegisterProvider(value = DnbLookupCounterFilter.class, priority = 9)
@RateLimit(value = 1)
@Path("/sru")
public interface DnbLookupClient {
    String CACHE_NAME = "dnb-lookup";
    long CACHE_LOCK_TIMEOUT = 10L;

    /**
     * This is the generic query to the DNB index.
     *
     * <p>Will result in something like:</p>
     * <pre>https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3D9783958672567&recordSchema=MARC21-xml</pre>
     *
     * @param query The query to be sent. It has to contain the index followed by a '='. The nicest index is 'WOE'.
     * @return A set of Books.
     */
    @Timed("ean-search.lookup.ean.time")
    @Counted("ean-search.lookup.ean.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            abortOn = DnbLookupException.class
    )
    @CircuitBreaker(
            failOn = DnbLookupException.class,
            requestVolumeThreshold = 5
    )
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    @GET
    @Path("/dnb")
    @Produces(TEXT_XML)
    @ClientQueryParams({
            @ClientQueryParam(name = "version", value = "1.1"),
            @ClientQueryParam(name = "operation", value = "searchRetrieve"),
            @ClientQueryParam(name = "recordSchema", value = "rdf")
    })
    SearchRetrieveResponse dnbLookup(@QueryParam("query") final String query);
}
