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

package de.kaiserpfalzedv.services.eansearch.mapper;

import org.springframework.stereotype.Service;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * <p>ResponseErrorMapper -- Filters for HTTP Status codes of the API</p>
 *
 * <p>The status codes are documented in the
 * <a href="https://www.ean-search.org/premium/ean-api.html#__RefHeading___Toc147_3132899627">EAN-Search documentation,
 * Appendix A</a>. This mapper maps them to the runtime exceptions for a better handling.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@Service
public class ResponseErrorMapper implements ErrorDecoder {
    public static final int INVALID_OPERATION = 400;
    public static final int INVALID_ACCESS_TOKEN = 401;
    public static final int REQUEST_LIMIT_REACHED = 402;
    public static final int INVALID_HTTP_METHOD = 405;
    public static final int RATE_LIMIT_REACHED = 429;

    @Override
    public EanSearchException decode(final String methodKey, final Response response) {
        return switch (response.status()) {
            case INVALID_OPERATION -> new EanSearchInvalidOperationException();
            case INVALID_ACCESS_TOKEN -> new EanSearchInvalidAccessTokenException();
            case REQUEST_LIMIT_REACHED -> new EanSearchRequestLimitReachedException();
            case INVALID_HTTP_METHOD -> new EanSearchWrongHTTPMethodException();
            case RATE_LIMIT_REACHED -> new EanSearchTooManyRequestsException();
            default -> null;
        };

    }
}
