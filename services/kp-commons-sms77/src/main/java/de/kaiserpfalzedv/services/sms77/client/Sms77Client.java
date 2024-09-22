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

package de.kaiserpfalzedv.services.sms77.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.kaiserpfalzedv.services.sms77.model.Balance;
import de.kaiserpfalzedv.services.sms77.model.NumberFormatCheckResult;
import de.kaiserpfalzedv.services.sms77.model.Sms;
import de.kaiserpfalzedv.services.sms77.model.SmsResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * <p>
 * Sms77Client -- The client for accessing the webservice.
 * </p>
 *
 * <p>
 * This is the client for accessing the API of the sms77 paid webservice. You need an Api-Key. This client has a spring boot application.yaml included which will rely on certain environment variables to be set. These are:
 * </p>
 *
 * <dl>
 * <dt>SMS77_API_URL</dt>
 * <dd><em>(optional)</em>The URI for the SMS77.io api. Normally there is no reason to give another URI than {@literal https://gateway.sms77.io}. And that URI is the default when nothing else is specified./</dd>
 * <dt>SMS77_API_KEY</dt>
 * <dd>The API key from sms77.io. For development you should generate a sandbox api key to cut costs - but your mileage may vary.</dd>
 * </dl>
 *
 * <p>
 * For the time being the API does not throw any sms77 specific exceptions since the sms77 API always returns HTTP 200. You have to check the return objects of the calls to find out if there has something happened.
 * </p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.0.0  2024-09-22
 * @since 3.0.0  2023-01-17
 */
@FeignClient(name = "sms77", configuration = Sms77ClientConfig.class, path = "/api")
@SuppressWarnings("JavadocLinkAsPlainText")
@RateLimiter(name = "sms77client")
public interface Sms77Client {
    /**
     * Sends the SMS.
     *
     * @param sms The SMS to be sent. The same SMS can address multiple users.
     * @return The result of the SMS sending.
     */
    @Timed("sms77.send-sms-json.time")
    @Counted("sms77.send-sms-json.count")
    @Retry(name = "sendSMS")
    @CircuitBreaker(name = "sendSMS")
    @PostMapping(value = "/sms", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    SmsResult sendSMS(@NotNull final Sms sms);

    /**
     * Sends the given text to the numbers specified.
     *
     * @param number A set of destinations for the SMS.
     * @param text   The text of the SMS.
     * @return
     */
    @Timed("sms77.send-sms-query.time")
    @Counted("sms77.send-sms-query.count")
    @Retry(name = "sendSMS")
    @CircuitBreaker(name = "sendSMS")
    @PostMapping(value = "/sms", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    SmsResult sendSMS(
            @Size(min = 1, max = 10) @RequestParam("to") @NotNull final Set<String> number,

            @Size(max = 1520) @RequestParam("text") @NotNull final String text);

    /**
     * To check the current credits to use on this API you can call the balance and
     * get the current credits.
     *
     * @return The current account balance of your sms77.io account.
     */
    @Timed("sms77.balance.time")
    @Counted("sms77.balance.count")
    @Retry(name = "balanceSMS")
    @CircuitBreaker(name = "balanceSMS")
    @GetMapping(value = "/balance", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Balance balance();

    /**
     * <p>
     * Check if the given numbers are formatted correctly. There will be no check,
     * if the numbers are valid. This
     * <p>
     * check only validates the number format and not if the number is used or even
     * active.
     * </p>
     *
     * <p>
     * This is a very ugly API call since the numbers need to be formated as single
     * string with a comma as
     * delimiter.
     * </p>
     *
     * <p>
     * Consider something as Set.of("49123231","124323131").join(",") ...
     * </p>
     *
     * @param numbersWithComma The numbers to check, delimited by a comma.
     * @return The format check result.
     */
    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(name = "lookupSMS")
    @CircuitBreaker(name = "lookupSMS")
    @GetMapping(value = "/lookup", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Set<NumberFormatCheckResult> checkMultipleNumberFormats(
            @RequestParam("number") @NotBlank final String numbersWithComma);

    /**
     * <p>
     * Checks the format of a single number.
     * </p>
     *
     * <p>
     * This function only checks, if the number format is correct. There is no check
     * if the number is assigned or
     * even activ.
     * </p>
     *
     * @param number the number which format should be checked.
     * @return The format check result.
     */
    @Timed("sms77.number-format-check.multi.time")
    @Counted("sms77.number-format-check.multi.count")
    @Retry(name = "checkNumberFormatSMS")
    @CircuitBreaker(name = "checkNumberFormatSMS")
    @GetMapping(value = "/lookup", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    NumberFormatCheckResult checkNumberFormat(@RequestParam("number") @NotBlank final String number);
}
