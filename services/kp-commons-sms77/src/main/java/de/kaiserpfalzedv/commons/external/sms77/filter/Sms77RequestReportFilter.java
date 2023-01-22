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

package de.kaiserpfalzedv.commons.external.sms77.filter;

import de.kaiserpfalzedv.commons.external.sms77.mapper.Sms77Exception;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * <p>Sms77RequestReportFilter -- Reports the send SMS.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class Sms77RequestReportFilter implements ClientRequestFilter {
    private static final String API_REQUESTS_HANDLED_SINCE_START = "sms77.sms-since-start";

    private final MeterRegistry registry;
    private Counter requestCounter;

    @PostConstruct
    public void registerMetric() {
        requestCounter = registry.counter(API_REQUESTS_HANDLED_SINCE_START, Tags.empty());
    }

    @PreDestroy
    public void unregisterMetrics() {
        requestCounter.close();
    }

    /**
     * This filter prevents the external call when there are no credits left for this API.
     *
     * @param requestContext request context.
     * @throws Sms77Exception When the credits for this API has been used and there are no
     *                                               credits remaining.
     */
    @Override
    public void filter(ClientRequestContext requestContext) throws Sms77Exception {
        requestCounter.increment();
    }
}
