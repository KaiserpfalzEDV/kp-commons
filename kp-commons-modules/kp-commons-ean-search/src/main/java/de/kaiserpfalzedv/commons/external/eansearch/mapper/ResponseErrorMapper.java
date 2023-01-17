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

package de.kaiserpfalzedv.commons.external.eansearch.mapper;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

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
@Provider
public class ResponseErrorMapper implements ResponseExceptionMapper<RuntimeException> {
    @Override
    public EanSearchException toThrowable(final Response response) {
        switch (response.getStatus()) {
            case 400: return new EanSearchInvalidOperationException();
            case 401: return new EanSearchInvalidAccessTokenException();
            case 402: return new EanSearchRequestLimitReachedException();
            case 405: return new EanSearchWrongHTTPMethodException();
            case 429: return new EanSearchTooManyRequestsException();
        }

        return null;
    }
}
