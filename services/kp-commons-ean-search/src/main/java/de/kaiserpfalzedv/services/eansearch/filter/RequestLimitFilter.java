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

package de.kaiserpfalzedv.services.eansearch.filter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;

import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchException;
import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchTooManyRequestsException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>RequestLimitFilter -- Reports the used credits and the remaining credits for the EAN search api.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-17
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "lombok created constructor used.")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RequestLimitFilter implements RequestInterceptor, ResponseInterceptor {
    private static final String API_REMAINING_REQUEST_HEADER = "X-Credits-Remaining";
    private static final String API_REMAINING_METRICS_NAME = "ean-search.credits.remaining";
    private static final String API_REQUESTS_HANDLED_SINCE_START = "ean-search.credits.used-since-start";

    private final MeterRegistry registry;
    private Counter requestCounter;

    // No lombok generateion to make it synchronized
    private int remaining = -1;
    private OffsetDateTime lastRequest = OffsetDateTime.now(ZoneOffset.UTC).minus(1, ChronoUnit.DAYS);

    @PostConstruct
    public void registerMetric() {
        this.requestCounter = this.registry.counter(API_REQUESTS_HANDLED_SINCE_START, Tags.empty());
    }

    @PreDestroy
    public void unregisterMetrics() {
        this.requestCounter.close();
    }

    public synchronized int getRemaining() {
        return this.remaining;
    }

    /**
     * Resets the counter and enables new requests, that would otherwise being blocked by this filter.
     */
    public void reset() {
        synchronized(this) {
            this.remaining = -1;
            log.info("EAN Search credit reporter reset. Requests should now result in requests to the API.");
        }
    }



    /**
     * Reports the handled requests and remaining credits to the API.
     *
     * @param context  request context.
     * @param chain the remaining chain to be worked on.
     */
    @Override
    public Object intercept(final InvocationContext context, final Chain chain) throws Exception {
        final String remainingRequestCount = context.response().headers().get(API_REMAINING_REQUEST_HEADER).stream().findFirst().orElse(null);

        synchronized (this) {
            if (remainingRequestCount != null) {
                remaining = Integer.valueOf(remainingRequestCount, 10);
                registry.gauge(API_REMAINING_METRICS_NAME, Tags.empty(), remaining);
            }
        }

        log.debug("EAN-Search remaining requests. remaining={}, used={}", remaining, requestCounter.count());
        return chain.next(context);
    }

    /**
     * This filter prevents the external call when there are no credits left for this API.
     *
     * @param request The request teamplate to use.
     * @throws EanSearchException When the credits for this API has been used and there are no
     *                                               credits remaining.
     */
    @Override
    public void apply(final RequestTemplate request) throws EanSearchException {
        if (
                this.remaining == 0
                && this.lastRequestWasToday()
        ) {
            log.error("Can't search for EAN any more. There is no remaining credit left. lastRequest='{}',", this.lastRequest);

            throw new EanSearchTooManyRequestsException();
        }

        synchronized (this) {
            this.lastRequest = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    private boolean lastRequestWasToday() {
        return this.lastRequest.isAfter(
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toOffsetDateTime()
        );
    }
}
