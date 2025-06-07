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

package de.kaiserpfalzedv.services.eansearch.filter;


import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class QueryParamFilter implements ExchangeFilterFunction {
  /**
   * format for lookups
   */
  private static final String FORMAT = "json";
  /**
   * lookup type
   */
  private static final String OP = "barcode-lookup";
  
  /**
   * The API-Key for accessing the EAN-Search API
   */
  @Value("${eansearch.token}")
  private String apiKey;
  
  @Value("${eansearch.language}")
  private String language;
  
  private String additionalQueryParams;
  
  @PostConstruct
  public void init() {
    log.entry(apiKey, language);
    
    additionalQueryParams = String.format("&format=%s&op=%s&language=%s&token=%s", FORMAT, OP, language, apiKey);
    
    log.exit(additionalQueryParams);    // Initialize any additional parameters if needed
  }
  
  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    ClientRequest modifiedRequest = ClientRequest.from(request)
        .url(request.url().resolve(request.url() + additionalQueryParams))
        .build();
    
    return next.exchange(modifiedRequest);
  }
}