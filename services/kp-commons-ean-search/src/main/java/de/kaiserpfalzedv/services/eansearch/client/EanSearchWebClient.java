/*
 * Copyright (c) 2023-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.eansearch.client;

import de.kaiserpfalzedv.services.eansearch.filter.QueryParamFilter;
import de.kaiserpfalzedv.services.eansearch.filter.RequestLimitFilter;
import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchErrorFilter;
import de.kaiserpfalzedv.services.eansearch.model.EanData;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * EanSearchClient -- The client for accessing the web service.
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@XSlf4j
public class EanSearchWebClient {
  private final WebClient.Builder webClientBuilder;
  private final RequestLimitFilter requestLimitFilter;
  private final QueryParamFilter queryParamFilter;
  private final EanSearchErrorFilter errorFilter;
  
  @Value("${eansearch.base-url:https://www.ean-search.org}")
  private String baseUrl;
  
  private WebClient client;
  
  /**
   * Initializes the WebClient with the base URL and filters.
   */
  @PostConstruct
  public void init() {
    log.entry(webClientBuilder, requestLimitFilter, baseUrl);
    
    client = webClientBuilder.baseUrl(baseUrl).filter(requestLimitFilter).filter(queryParamFilter)
                             .filter(errorFilter)
                             .defaultHeader("Accept", "application/json")
                             .build();
    
    log.exit(client);
  }
  
  /**
   * barcodeLookupEAN -- Looks up EAN data by EAN-13 code.
   *
   * @param ean13 The EAN-13 code to look up.
   * @return the set of EanData objects matching the EAN-13 code.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public Set<EanData> barcodeLookupEAN(String ean13) {
    log.entry(ean13);
    
    return log.exit(
        client.get().uri(uriBuilder -> uriBuilder.path("/domain")
                                                 .queryParam("ean", ean13)
                                                 .build())
              .retrieve().bodyToFlux(EanData.class).collectList().map(Set::copyOf).block());
  }

  /**
   * barcodeLookupUPC -- Looks up EAN data by UPC code.
   *
   * @param upc12 The UPC code to look up.
   * @return the set of EanData objects matching the UPC code.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public Set<EanData> barcodeLookupUPC(String upc12) {
    log.entry(upc12);
    
    return log.exit(
        client.get().uri(uriBuilder -> uriBuilder.path("/domain")
                                                 .queryParam("upc", upc12).build())
              .retrieve().bodyToFlux(EanData.class).collectList().map(Set::copyOf).block());
  }
  
  /**
   * barcodeLookupISBN -- Looks up EAN data by ISBN-10 code.
   *
   * @param isbn10 The ISBN-10 code to look up.
   * @return the set of EanData objects matching the ISBN-10 code.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public Set<EanData> barcodeLookupISBN(String isbn10) {
    log.entry(isbn10);
    
    return log.exit(
        client.get().uri(uriBuilder -> uriBuilder.path("/domain")
                                                 .queryParam("isbn", isbn10).build())
                          .retrieve()
                          .bodyToFlux(EanData.class).collectList().map(Set::copyOf).block());
  }
}