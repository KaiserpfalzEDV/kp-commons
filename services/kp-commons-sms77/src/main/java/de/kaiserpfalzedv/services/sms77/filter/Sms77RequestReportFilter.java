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

package de.kaiserpfalzedv.services.sms77.filter;

import de.kaiserpfalzedv.services.sms77.mapper.Sms77Exception;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import feign.InvocationContext;
import feign.ResponseInterceptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * <p>Sms77RequestReportFilter -- Reports the send SMS.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Sms77RequestReportFilter implements ResponseInterceptor {
    private static final String API_REQUESTS_HANDLED_SINCE_START = "sms77.sms-since-start";

    private final MeterRegistry registry;
    private Counter requestCounter;

    @PostConstruct
    public void registerMetric() {
        this.requestCounter = this.registry.counter(API_REQUESTS_HANDLED_SINCE_START, Tags.empty());
    }

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "The field is set in the PostConstruct method of this class.")
    @PreDestroy
    public void unregisterMetrics() {
        assert this.registry != null;

        this.requestCounter.close();
    }

    /**
     * This filter prevents the external call when there are no credits left for this API.
     *
     * @param requestContext request context.
     * @throws Sms77Exception When the credits for this API has been used and there are no
     *                                               credits remaining.
     */

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "The field is set in the PostConstruct method of this class.")
    @Override
    public Object intercept(final InvocationContext invocationContext, final Chain chain) throws Exception {
        assert this.registry != null;

        this.requestCounter.increment();

        return chain.next(invocationContext);
    }
}
