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
package de.kaiserpfalzedv.services.eansearch.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.Setter;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-11
 */
// No @Configuration - otherwise it would be the default for all feign clients
public class EanSearchClientConfig {
    /** format for lookups */
    private static final String FORMAT = "json";
    /** lookup type */
    private static final String OP = "barcode-lookup";

    /** The API-Key for accessing the EAN-Search API */
    @Value("${eansearch.token}")
    @Setter
    private String apiKey;

    @Bean
    public RequestInterceptor eanSearchDefaultParameters(@Value("${eansearch.token}") final String apiKey, @Value("${eansearch.language}") final String language) {
        return requestTemplate -> requestTemplate
            .query("format", FORMAT)
            .query("op", OP)
            .query("language", language)
            .query("token", apiKey)
            ; 
    }


    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }
}
