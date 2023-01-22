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

package de.kaiserpfalzedv.commons.external.sms77.client;

import de.kaiserpfalzedv.commons.external.sms77.filter.Sms77RequestReportFilter;
import de.kaiserpfalzedv.commons.external.sms77.mapper.ResponseErrorMapper;
import de.kaiserpfalzedv.commons.external.sms77.mapper.Sms77Exception;
import de.kaiserpfalzedv.commons.external.sms77.mapper.Sms77RateLimitException;
import de.kaiserpfalzedv.commons.external.sms77.model.Balance;
import de.kaiserpfalzedv.commons.external.sms77.model.NumberFormatCheckResult;
import de.kaiserpfalzedv.commons.external.sms77.model.Sms;
import de.kaiserpfalzedv.commons.external.sms77.model.SmsResult;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.ClientQueryParams;
import io.smallrye.faulttolerance.api.RateLimit;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParams;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * <p>Sms77Client -- The client for accessing the webservice.</p>
 *
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@RegisterRestClient
@RegisterProvider(value = ResponseErrorMapper.class, priority = 10)
@RegisterProvider(value = Sms77RequestReportFilter.class, priority = 9)
@ClientQueryParams({
        @ClientQueryParam(name = "json", value = "1"),
})
@ClientHeaderParams({
        @ClientHeaderParam(name = "X-Api-Key", value = "${sms77.token}")
})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RateLimit(value = 50)
@Path("/api")
public interface Sms77Client {
    @Timed("sms77.send-sms-json.time")
    @Counted("sms77.send-sms-json.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @POST
    @Path("/sms")
    SmsResult sendSMS(@NotNull final Sms sms);


    @Timed("sms77.send-sms-query.time")
    @Counted("sms77.send-sms-query.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @GET
    @Path("/sms")
    SmsResult sendSMS(
            @Size(min = 1, max= 10)
            @QueryParam("to") @NotNull final Set<String> number,

            @Size(max = 1520)
            @QueryParam("text") @NotNull final String text
    );


    @Timed("sms77.balance.time")
    @Counted("sms77.balance.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @GET
    @Path("/balance")
    Balance balance();

    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @GET
    @ClientQueryParam(name = "type", value = "format")
    @Path("/lookup")
    Set<NumberFormatCheckResult> checkMultipleNumberFormats(@QueryParam("number") @NotBlank final String numbersWithComma);

    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(
            delay = 2000,
            maxDuration = 6000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @GET
    @ClientQueryParam(name = "type", value = "format")
    @Path("/lookup")
    NumberFormatCheckResult checkNumberFormat(@QueryParam("number") @NotBlank final String number);
}
