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

package de.kaiserpfalzedv.services.sms77.client;

import de.kaiserpfalzedv.services.sms77.mapper.Sms77ResponeMapper;
import de.kaiserpfalzedv.services.sms77.model.Balance;
import de.kaiserpfalzedv.services.sms77.model.NumberFormatCheckResult;
import de.kaiserpfalzedv.services.sms77.model.Sms;
import de.kaiserpfalzedv.services.sms77.model.SmsResult;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class Sms77WebClient implements Sms77Client {
  
  private final WebClient.Builder webClientBuilder;
  private final Sms77ResponeMapper sms77ResponseMapper;
  
  @Value("${sms77.api.url}")
  private String baseUrl;
  
  @Value("${sms77.token}")
  private String apiKey;
  
  private WebClient webClient;
  
  @PostConstruct
  public void init() {
    log.entry(baseUrl);
    
    this.webClient = webClientBuilder
        .baseUrl(baseUrl)
        .defaultHeader("json", "1")
        .defaultHeader("type", "format")
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .defaultHeader("X-Api-Key", apiKey)
        .filter(sms77ResponseMapper.messageFilter())
        .build();
    
    log.exit(webClient);
  }
  
  
  @Timed("sms77.send-sms-json.time")
  @Counted("sms77.send-sms-json.count")
  @Override
  public SmsResult sendSMS(Sms sms) {
    log.entry(sms);
    
    return log.exit(webClient.post()
        .uri("/sms")
        .bodyValue(sms)
        .retrieve()
        .bodyToMono(SmsResult.class)
        .block());
  }
  
  @Timed("sms77.send-sms-query.time")
  @Counted("sms77.send-sms-query.count")
  @Override
  public SmsResult sendSMS(Set<String> number, String text) {
    log.entry(number, text);
    
    return log.exit(webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/sms")
            .queryParam("to", String.join(",", number))
            .queryParam("text", text)
            .build())
        .retrieve()
        .bodyToMono(SmsResult.class)
        .block());
  }
  
  @Timed("sms77.balance.time")
  @Counted("sms77.balance.count")
  @Override
  public Balance balance() {
    log.entry();
    
    return log.exit(webClient.get()
        .uri("/balance")
        .retrieve()
        .bodyToMono(Balance.class)
        .block());
  }
  
  @Timed("sms77.number-format-check.multi.time")
  @Counted("sms77.number-format-check.multi.count")
  @Override
  public Set<NumberFormatCheckResult> checkMultipleNumberFormats(String numbersWithComma) {
    log.entry(numbersWithComma);
    
    List<NumberFormatCheckResult> result = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/lookup")
            .queryParam("number", numbersWithComma)
            .build())
        .retrieve()
        .bodyToFlux(NumberFormatCheckResult.class)
        .collectList()
        .block();
    
    return log.exit(Set.copyOf(result != null ? result : List.of()));
  }
  
  @Timed("sms77.number-format-check.multi.time")
  @Counted("sms77.number-format-check.multi.count")
  @Override
  public NumberFormatCheckResult checkNumberFormat(String number) {
    log.entry(number);
  
    return log.exit(webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/lookup")
            .queryParam("number", number)
            .build())
        .retrieve()
        .bodyToMono(NumberFormatCheckResult.class)
        .block());
  }
}