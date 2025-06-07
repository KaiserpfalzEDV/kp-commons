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

package de.kaiserpfalzedv.services.eansearch.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * <p>EanSearchErrorFilter -- Filters for HTTP Status codes of the API</p>
 *
 * <p>The status codes are documented in the
 * <a href="https://www.ean-search.org/premium/ean-api.html#__RefHeading___Toc147_3132899627">EAN-Search documentation,
 * Appendix A</a>. This mapper maps them to the runtime exceptions for a better handling.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@Component
@XSlf4j
public class EanSearchErrorFilter implements ExchangeFilterFunction {
    public static final int INVALID_OPERATION = 400;
    public static final int INVALID_ACCESS_TOKEN = 401;
    public static final int REQUEST_LIMIT_REACHED = 402;
    public static final int INVALID_HTTP_METHOD = 405;
    public static final int RATE_LIMIT_REACHED = 429;
    
    @Override
    public Mono<ClientResponse> filter(@NotNull final ClientRequest request, @NotNull final ExchangeFunction next) {
        log.entry(request, next);
        
        return log.exit(
            next.exchange(request)
            .flatMap(response -> {
                int statusCode = response.statusCode().value();
              return switch (statusCode) {
                case INVALID_OPERATION -> Mono.error(new EanSearchInvalidOperationException());
                case INVALID_ACCESS_TOKEN -> Mono.error(new EanSearchInvalidAccessTokenException());
                case REQUEST_LIMIT_REACHED -> Mono.error(new EanSearchRequestLimitReachedException());
                case INVALID_HTTP_METHOD -> Mono.error(new EanSearchWrongHTTPMethodException());
                case RATE_LIMIT_REACHED -> Mono.error(new EanSearchTooManyRequestsException());
                default -> Mono.just(response);
              };
            })
        );
    }
}
