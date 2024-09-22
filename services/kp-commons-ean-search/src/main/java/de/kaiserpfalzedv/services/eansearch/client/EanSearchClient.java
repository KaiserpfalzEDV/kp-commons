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

package de.kaiserpfalzedv.services.eansearch.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.kaiserpfalzedv.services.eansearch.model.EanData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;

/**
 * <p>EanSearchClient -- The client for accessing the web service.</p>
 *
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@FeignClient(name = "eansearch", configuration = EanSearchClientConfig.class, path = "/api")
public interface EanSearchClient {
    @GetMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Retry(name = "eansearch")
    @CircuitBreaker(name = "eansearch")
    @Timed("ean-search.lookup.ean.time")
    @Counted("ean-search.lookup.ean.count")
    // @Retry(delay = 2000, maxDuration = 6000, retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class}, abortOn = EanSearchException.class)
    // @CircuitBreaker(failOn = EanSearchException.class, requestVolumeThreshold = 5)
    Set<EanData> barcodeLookupEAN(@RequestParam("ean") final String ean13);

    @GetMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Retry(name = "eansearch")
    @CircuitBreaker(name = "eansearch")
    @Timed("ean-search.lookup.upc.time")
    @Counted("ean-search.lookup.upc.count")
    // @Retry(delay = 2000, maxDuration = 6000, retryOn = {EanSearchTooManyRequestsException.class, RateLimitException.class}, abortOn = EanSearchException.class)
    // @CircuitBreaker(failOn = EanSearchException.class, requestVolumeThreshold = 5)
    Set<EanData> barcodeLookupUPC(@RequestParam("upc") final String upc12);

    @GetMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Retry(name = "eansearch")
    @CircuitBreaker(name = "eansearch")
    @Timed("ean-search.lookup.isbn.time")
    @Counted("ean-search.lookup.isbn.count")
    Set<EanData> barcodeLookupISBN(@RequestParam("isbn") final String isbn10);
}
