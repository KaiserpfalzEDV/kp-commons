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

package de.kaiserpfalzedv.commons.external.eansearch.filter;

import de.kaiserpfalzedv.commons.external.eansearch.mapper.EanSearchException;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.ResponseErrorMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * <p>RequestLimitFilter -- Reports the used credits and the remaining credits for the EAN search api.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-17
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class RequestLimitFilter implements ClientResponseFilter, ClientRequestFilter {
    private static final String API_REMAINING_REQUEST_HEADER = "X-Credits-Remaining";
    private static final String API_REMAINING_METRICS_NAME = "ean-search.credits.remaining";
    private static final String API_REQUESTS_HANDLED_SINCE_START = "ean-search.credits.used-since-start";

    private final MeterRegistry registry;
    private Counter requestCounter;

    @Getter
    private int remaining = -1;
    private OffsetDateTime lastRequest = OffsetDateTime.now(ZoneOffset.UTC).minus(1, ChronoUnit.DAYS);

    @PostConstruct
    public void registerMetric() {
        registry.gauge(API_REMAINING_METRICS_NAME, Tags.empty(), remaining);
        requestCounter = registry.counter(API_REQUESTS_HANDLED_SINCE_START, Tags.empty());
    }

    @PreDestroy
    public void unregisterMetrics() {
        requestCounter.close();
    }

    /**
     * Resets the counter and enables new requests, that would otherwise being blocked by this filter.
     */
    public synchronized void reset() {
        remaining = -1;
        log.info("EAN Search credit reporter reset. Requests should now result in requests to the API.");
    }

    /**
     * Reports the handled requests and remaining credits to the API.
     *
     * @param requestContext  request context.
     * @param responseContext response context.
     */
    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) {
        String remaining = responseContext.getHeaderString(API_REMAINING_REQUEST_HEADER);

        synchronized (this) {
            this.remaining = Integer.valueOf(remaining, 10);
            requestCounter.increment();
        }

        log.debug("EAN-Search remaining requests. remaining={}, used={}", this.remaining, requestCounter.count());
    }

    /**
     * This filter prevents the external call when there are no credits left for this API.
     *
     * @param requestContext request context.
     * @throws EanSearchException When the credits for this API has been used and there are no
     *                                               credits remaining.
     */
    @Override
    public void filter(ClientRequestContext requestContext) throws EanSearchException {
        if (
                remaining == 0
                && lastRequestWasToday()
        ) {
            log.error("Can't search for EAN any more. There is no remaining credit left. lastRequest='{}',", lastRequest);

            requestContext.abortWith(Response.status(ResponseErrorMapper.REQUEST_LIMIT_REACHED).build());
        }

        synchronized (this) {
            lastRequest = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    private boolean lastRequestWasToday() {
        return lastRequest.isAfter(
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toOffsetDateTime()
        );
    }
}
