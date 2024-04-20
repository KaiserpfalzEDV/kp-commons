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
package de.kaiserpfalzedv.services.dnb.client;

import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.kaiserpfalzedv.services.dnb.marcxml.MarcConverter;
import feign.Logger;
import feign.RequestInterceptor;

/**
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-11
 */
// No @Configuration - otherwise it would be the default for all feign clients
public class DnbLookupClientConfig {
    private static final String VERSION = "1.1";
    private static final String OPERATION = "searchRetrieve";
    private static final String RECORD_SCHEMA = "MARC21-xml";


    @Bean
    public RequestInterceptor dnbDefaultParameters() {
        return requestTemplate -> requestTemplate
            .query("operation", OPERATION)
            .query("version", VERSION)
            .query("recordSchema", RECORD_SCHEMA)
            ;
    }

    @Bean
    public ResponseEntityDecoder dnbDecoder() {
        return new ResponseEntityDecoder(new Marc21Decoder(new MarcConverter(new ObjectMapper())));
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }
}
