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

package de.kaiserpfalzedv.services.eansearch.filter;

import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchTooManyRequestsException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * <p>RequestLimitFilter -- Reports the used credits and the remaining credits for the EAN search api.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-17
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "lombok created constructor used.")
@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class RequestLimitFilter implements ExchangeFilterFunction {
    private static final String API_REMAINING_REQUEST_HEADER = "X-Credits-Remaining";
    private static final String API_REMAINING_METRICS_NAME = "ean-search.credits.remaining";
    private static final String API_REQUESTS_HANDLED_SINCE_START = "ean-search.credits.used-since-start";

    private final MeterRegistry registry;
    private Counter requestCounter;

    // No lombok generation to make it synchronized
    private int remaining = -1;
    private OffsetDateTime lastRequest = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);

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
     * @param request  request context.
     * @param next the remaining chain to be worked on.
     */
    @Override
    public Mono<ClientResponse> filter(@NotNull ClientRequest request, @NotNull ExchangeFunction next) {
        log.entry(request, next);
        
        synchronized (this) {
            if (remaining == 0 && lastRequestWasToday()) {
                log.error("No Credits left for EAN Search API. lastRequest='{}'", lastRequest);
                return Mono.error(log.throwing(new EanSearchTooManyRequestsException()));
            }
            lastRequest = OffsetDateTime.now(ZoneOffset.UTC);
        }
        
        return log.exit(
          next.exchange(request)
            .doOnNext(response -> {
                final String remainingRequestCount = response.headers().header(API_REMAINING_REQUEST_HEADER).stream().findFirst().orElse(null);
                if (remainingRequestCount != null) {
                    synchronized (this) {
                        remaining = Integer.parseInt(remainingRequestCount, 10);
                        registry.gauge(API_REMAINING_METRICS_NAME, Tags.empty(), remaining);
                    }
                    log.debug("Remaining requests in response header: {}", remainingRequestCount);
                }
            })
        );
        
    }
    
    @Override
    public ExchangeFilterFunction andThen(final @NotNull ExchangeFilterFunction afterFilter) {
        return ExchangeFilterFunction.super.andThen(afterFilter);
    }
    
    private boolean lastRequestWasToday() {
        return this.lastRequest.isAfter(
                OffsetDateTime.now(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toOffsetDateTime()
        );
    }
}
