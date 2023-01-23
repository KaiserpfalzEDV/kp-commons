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
 * <p>This is the client for accessing the API of the sms77 paid webservice. You need an Api-Key. This client has a
 * quarkus application.yaml included which will rely on certain environment variables to be set. These are:</p>
 *
 * <dl>
 *  <dt>SMS77_API_URL</dt>
 *  <dd><em>(optional)</em>The URI for the SMS77.io api. Normally there is no reason to give another URI than
 *  {@literal https://gateway.sms77.io}. And that URI is the default when nothing else is specified./</dd>
 *  <dt>SMS77_API_KEY</dt><dd>The API key from sms77.io. For development you should generate a sandbox api key to cut
 *  costs - but your mileage may vary.</dd>
 * </dl>
 *
 * <p>For the time being the API does not throw any sms77 specific exceptions since the sms77 API always returns
 * HTTP 200. You have to check the return objects of the calls to find out if there has something happened.</p>
 *
 * <p><em
 * >TODO 2023-01-22 rlichti Implement a filter to read the objects and generate matching exceptions.
 * <br/>NOTE: I don't have a direct need for this, but it would be a much nicer interface for accessing the sms77 api.
 * </em></p>
 *
 *
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@SuppressWarnings("JavadocLinkAsPlainText")
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
    /**
     * Sends the SMS.
     *
     * @param sms The SMS to be sent. The same SMS can address multiple users.
     * @return The result of the SMS sending.
     */
    @Timed("sms77.send-sms-json.time")
    @Counted("sms77.send-sms-json.count")
    @Retry(
            delay = 1000,
            maxDuration = 5000,
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


    /**
     * Sends the given text to the numbers specified.
     * @param number A set of destinations for the SMS.
     * @param text The text of the SMS.
     * @return
     */
    @Timed("sms77.send-sms-query.time")
    @Counted("sms77.send-sms-query.count")
    @Retry(
            delay = 1000,
            maxDuration = 5000,
            retryOn = {Sms77RateLimitException.class},
            abortOn = Sms77Exception.class
    )
    @CircuitBreaker(
            failOn = Sms77Exception.class,
            requestVolumeThreshold = 5
    )
    @POST
    @Path("/sms")
    SmsResult sendSMS(
            @Size(min = 1, max= 10)
            @QueryParam("to") @NotNull final Set<String> number,

            @Size(max = 1520)
            @QueryParam("text") @NotNull final String text
    );


    /**
     * To check the current credits to use on this API you can call the balance and get the current credits.
     *
     * @return The current account balance of your sms77.io account.
     */
    @Timed("sms77.balance.time")
    @Counted("sms77.balance.count")
    @Retry(
            delay = 100,
            maxDuration = 500,
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

    /**
     * <p>Check if the given numbers are formatted correctly. There will be no check, if the numbers are valid. This
     * <p>check only validates the number format and not if the number is used or even active.</p>
     *
     * <p>This is a very ugly API call since the numbers need to be formated as single string with a comma as
     * delimiter.</p>
     *
     * <p>Consider something as Set.of("49123231","124323131").join(",") ...</p>
     *
     * @param numbersWithComma The numbers to check, delimited by a comma.
     * @return The format check result.
     */
    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(
            delay = 200,
            maxDuration = 1000,
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

    /**
     * <p>Checks the format of a single number.</p>
     *
     * <p>This function only checks, if the number format is correct. There is no check if the number is assigned or
     * even activ.</p>
     *
     * @param number the number which format should be checked.
     * @return The format check result.
     */
    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(
            delay = 200,
            maxDuration = 1000,
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
