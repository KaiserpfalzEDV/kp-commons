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

package de.kaiserpfalzedv.commons.external.dnb.client;

import de.kaiserpfalzedv.commons.external.dnb.model.DnbLookupException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * <p>ResponseErrorMapper -- Filters for HTTP Status codes of the API</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-22
 */
@Provider
public class ResponseErrorMapper implements ResponseExceptionMapper<DnbLookupException> {
    @Override
    public DnbLookupException toThrowable(final Response response) {
        return null;
    }
}
