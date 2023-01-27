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

package de.kaiserpfalzedv.commons.external.eansearch.client;

import de.kaiserpfalzedv.commons.external.eansearch.filter.RequestLimitFilter;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.EanSearchException;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.EanSearchTooManyRequestsException;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.ResponseErrorMapper;
import de.kaiserpfalzedv.commons.external.eansearch.model.EanData;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.cache.CacheResult;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.ClientQueryParams;
import io.smallrye.faulttolerance.api.RateLimit;
import io.smallrye.faulttolerance.api.RateLimitException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * <p>EanSearchClient -- The client for accessing the webservice.</p>
 *
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@RegisterRestClient
@RegisterProvider(value = ResponseErrorMapper.class, priority = 10)
@RegisterProvider(value = RequestLimitFilter.class, priority = 9)
@ClientQueryParams({
        // the paid-for API token
        @ClientQueryParam(name = "token", value = "${ean_search.token}"),
        // We only like JSon
        @ClientQueryParam(name = "format", value = "json"),
        // the language according to https://www.ean-search.org/premium/ean-api.html#__RefHeading___Toc147_3132899627 Appendix B
        @ClientQueryParam(name = "language", value = "${ean_search.language}")
})
@RateLimit(value = 1)
@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EanSearchClient {
    String CACHE_NAME = "ean-search-org";
    long CACHE_LOCK_TIMEOUT = 10L;

    @Timed("ean-search.lookup.ean.time")
    @Counted("ean-search.lookup.ean.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class},
            abortOn = EanSearchException.class
    )
    @CircuitBreaker(
            failOn = EanSearchException.class,
            requestVolumeThreshold = 5
    )
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    @GET
    @ClientQueryParam(name = "op", value = "barcode-lookup")
    Set<EanData> barcodeLookupEAN(@QueryParam("ean") final String ean13);

    @Timed("ean-search.lookup.upc.time")
    @Counted("ean-search.lookup.upc.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class},
            abortOn = EanSearchException.class
    )
    @CircuitBreaker(
            failOn = EanSearchException.class,
            requestVolumeThreshold = 5
    )
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    @GET
    @ClientQueryParam(name = "op", value = "barcode-lookup")
    Set<EanData> barcodeLookupUPC(@QueryParam("upc") final String upc12);

    @Timed("ean-search.lookup.isbn.time")
    @Counted("ean-search.lookup.isbn.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class},
            abortOn = EanSearchException.class
    )
    @CircuitBreaker(
            failOn = EanSearchException.class,
            requestVolumeThreshold = 5
    )
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    @GET
    @ClientQueryParam(name = "op", value = "barcode-lookup")
    Set<EanData> barcodeLookupISBN(@QueryParam("isbn") final String isbn10);

    @Timed("ean-search.lookup.prefix.time")
    @Counted("ean-search.lookup.prefix.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class},
            abortOn = EanSearchException.class
    )
    @CircuitBreaker(
            failOn = EanSearchException.class,
            requestVolumeThreshold = 5
    )
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    @GET
    @ClientQueryParam(name = "op", value = "barcode-prefix-search")
    Set<EanData> barcodePrefixSearch(
            @QueryParam("prefix") final String prefix,
            @QueryParam("page") @DefaultValue("0") final int page
    );
}
