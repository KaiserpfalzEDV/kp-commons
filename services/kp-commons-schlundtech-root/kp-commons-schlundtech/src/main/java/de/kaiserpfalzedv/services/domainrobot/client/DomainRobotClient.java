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

package de.kaiserpfalzedv.services.domainrobot.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
 * This is the client for accessing the Domain Robot of Schlundtech. This client has a
 * quarkus application.yaml included which will rely on certain environment
 * variables to be set. These are:
 * </p>
 *
 * <dl>
 * <dt>DOMAINROBOT_API_URL</dt>
 * <dd><em>(optional)</em>The URI for the SMS77.io api. Normally there is no
 * reason to give another URI than
 * {@literal https://gateway.sms77.io}. And that URI is the default when nothing
 * else is specified./</dd>
 * <dt>DOMAINROBOT_API_KEY</dt>
 * <dd>The API key from sms77.io. For development you should generate a sandbox
 * api key to cut
 * costs - but your mileage may vary.</dd>
 * </dl>
 *
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.2.2 2024-01-20
 */
@FeignClient(name = "domainrobot", configuration = DomainRobotClientConfig.class, path = "/api")
@SuppressWarnings("JavadocLinkAsPlainText")
@RateLimiter(name = "domainrobotclient")
public interface DomainRobotClient {
    /**
     * Sends the SMS.
     *
     * @param sms The SMS to be sent. The same SMS can address multiple users.
     * @return The result of the SMS sending.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sms", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Timed("domainrobot.send-sms-json.time")
    @Counted("domainrobot.send-sms-json.count")
    @Retry(name = "sendSMS")
    @CircuitBreaker(name = "sendSMS")
//        delay = 1000, maxDuration = 5000, retryOn = {            Sms77RateLimitException.class }, abortOn = Sms77Exception.class)
//    @CircuitBreaker(failOn = Sms77Exception.class, requestVolumeThreshold = 5)
    int sendSMS(@NotNull final String variable);
}
