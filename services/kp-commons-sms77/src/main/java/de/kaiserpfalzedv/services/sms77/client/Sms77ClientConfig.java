/*
 * Copyright (c) 2023 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.kaiserpfalzedv.services.sms77.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import lombok.Setter;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-11
 */
// No @Configuration - otherwise it would be the default for all feign clients
public class Sms77ClientConfig {
    /** Query param 'json' */
    private static final String JSON = "1";

    /** format for lookups */
    private static final String FORMAT = "format";

    /** The API-Key for accessing the Seven.io-API */
    @Value("${sms77.token}")
    @Setter
    private String apiKey;

    @Bean
    public RequestInterceptor sevenIoDefaultParameters(@Value("${sms77.token}") final String apiKey) {
        return requestTemplate -> requestTemplate
            .query("json", JSON)
            .query("type", FORMAT)
            .header("X-Api-Key", apiKey)
            ; 
    }
}
