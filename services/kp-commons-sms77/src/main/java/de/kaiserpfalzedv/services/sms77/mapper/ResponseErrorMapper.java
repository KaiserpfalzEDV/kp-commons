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

package de.kaiserpfalzedv.services.sms77.mapper;

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
    public static final int SMS_FAILED_FOR_AT_LEAST_ONE_RECIPIENT = 101;
    public static final int INVALID_FROM = 201;
    public static final int INVALID_TO = 202;
    public static final int NO_TO = 301;
    public static final int NO_TEXT = 305;
    public static final int TEXT_TOO_LONG = 401;
    public static final int RELOAD_DETECTED = 402;
    public static final int MAX_DAILY_LIMIT_FOR_NUMBER = 403;
    public static final int NO_CREDITS_LEFT = 500;
    public static final int CARRIER_TRANSFER_FAILED = 600;
    public static final int AUTHENTICATION_FAILED = 900;
    public static final int SIGNATURE_CHECK_FAILED = 901;
    public static final int API_KEY_NOT_AUTHORIZED = 902;
    public static final int WRONG_SERVER_IP = 903;

    @Override
    public Exception decode(final String methodKey, final Response response) {
        return switch (response.status()) {
            case SMS_FAILED_FOR_AT_LEAST_ONE_RECIPIENT -> new Sms77SendingFailedException(101, "At least one recipient did not get the SMS.");
            case INVALID_FROM -> new Sms77SendingFailedException(201, "Invalid sender given.");
            case INVALID_TO -> new Sms77SendingFailedException(202, "Invalid recipient given.");
            case NO_TO -> new Sms77SendingFailedException(301, "No recipient specified.");
            case NO_TEXT -> new Sms77SendingFailedException(305, "No text specified.");
            case TEXT_TOO_LONG -> new Sms77SendingFailedException(401, "Text too long.");
            case CARRIER_TRANSFER_FAILED -> new Sms77SendingFailedException(500, "Carrier transfer failed.");

            case RELOAD_DETECTED -> new Sms77ReloadException();
            case MAX_DAILY_LIMIT_FOR_NUMBER -> new Sms77TooManyDailyCallsForThisNumberException();
            case NO_CREDITS_LEFT -> new Sms77NotEnoughCreditsException();

            case AUTHENTICATION_FAILED -> new Sms77InvalidAuthException("Invalid API key.");
            case SIGNATURE_CHECK_FAILED -> new Sms77InvalidAuthException("Signature does not match.");
            case API_KEY_NOT_AUTHORIZED -> new Sms77InvalidAuthException("API key not valid for this endpoint.");
            case WRONG_SERVER_IP -> new Sms77InvalidAuthException("Wrong server IP address.");

            default -> null;
        };
    }
}
