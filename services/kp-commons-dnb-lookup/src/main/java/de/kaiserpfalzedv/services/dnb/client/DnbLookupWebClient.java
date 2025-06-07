/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.dnb.client;

import de.kaiserpfalzedv.services.dnb.marcxml.MarcConverter;
import de.kaiserpfalzedv.services.dnb.model.Book;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


/**
 * @author klenkes74
 * @since 05.06.25
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class DnbLookupWebClient implements DnbLookupClient {
    private static final String VERSION = "1.1";
    private static final String OPERATION = "searchRetrieve";
    private static final String RECORD_SCHEMA = "MARC21-xml";
    
    private final WebClient.Builder webClientBuilder;
    private final MarcConverter marcConverter;
    
    @Value("${dnb-lookup.url}")
    private String baseUrl;
    
    private WebClient webClient;
    
    @PostConstruct
    public void init() {
        log.entry(baseUrl, marcConverter);
        
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        
        log.exit(webClient);
    }

    @Override
    public List<Book> lookup(String query) {
        log.entry(query);
        
        String xmlResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sru/dnb")
                        .queryParam("query", query)
                        .queryParam("operation", OPERATION)
                        .queryParam("version", VERSION)
                        .queryParam("recordSchema", RECORD_SCHEMA)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<Book> result = marcConverter.convert(xmlResponse);
        log.exit(result.size());
        return result;
    }
}