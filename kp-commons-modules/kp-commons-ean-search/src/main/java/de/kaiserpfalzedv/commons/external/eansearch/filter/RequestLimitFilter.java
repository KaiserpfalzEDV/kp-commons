/*
 * Copyright (c) 2023 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

/**
 * RequestLimitFilter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-17
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class RequestLimitFilter implements ClientResponseFilter {
    private static final String API_REMAINING_REQUEST_HEADER = "X-Credits-Remaining";
    private static final String API_REMAINING_METRICS_NAME = "ean-search.credits-remaining";

    private final MeterRegistry registry;

    @Getter
    private int remaining = 0;

    @PostConstruct
    public void registerMetric() {
        registry.gauge(API_REMAINING_METRICS_NAME, Tags.empty(), remaining);
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) {
        String remaining = responseContext.getHeaderString(API_REMAINING_REQUEST_HEADER);

        log.info("EAN-Search remaining requests. remaining='{}'", remaining);

        this.remaining = Integer.valueOf(remaining, 10);
    }
}
