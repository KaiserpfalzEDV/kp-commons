/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.core.rest;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpErrorGenerator -- Generates a {@link HttpProblem}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.1.0  2022-01-17
 */
@ApplicationScoped
@Slf4j
public class HttpErrorGenerator {
    @SuppressWarnings("unused")
    public HttpProblem throwHttpProblem(
            final Response.StatusType code,
            final String message,
            final Map<String, String> data,
            final Throwable cause
    ) {
        log.error(formatMessage(code, message, data), cause);
        return buildHttpError(code, message, data);
    }


    public HttpProblem throwHttpProblem(
            final Response.StatusType code,
            final String message,
            final Map<String, String> data
    ) {
        log.error(formatMessage(code, message, data));
        return buildHttpError(code, message, data);
    }


    private String formatMessage(final Response.StatusType code, final String message, final Map<String, String> data) {
        return String.format(
                "Returning HTTP Error to calling party. code=%d, title='%s', message='%s', data=%s",
                code.getStatusCode(),
                code.getReasonPhrase(),
                message,
                data.entrySet().stream()
                        .map(entry -> entry.getKey() + "='" + entry.getValue() + "'")
                        .collect(Collectors.joining(", ", "{", "}"))
        );
    }

    private HttpProblem buildHttpError(Response.StatusType code, String message, Map<String, String> data) {
        HttpProblem.Builder result = HttpProblem.builder()
                .withStatus(code)
                .withTitle(code.getReasonPhrase())
                .withDetail(message);

        data.forEach(result::with);

        return result.build();
    }
}
