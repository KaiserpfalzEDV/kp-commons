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

package de.kaiserpfalzedv.commons.users.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaiserpfalzedv.commons.api.events.BaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.events.ApiKeyBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.role.events.RoleBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserBaseEvent;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.Serial;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * A mapper to map user events to messages with content type {@link MimeTypes.APPLICATION_JSON}.
 * The encoding is {@link String} and not {@link byte[]}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-23
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString
@XSlf4j
public class UserEventMessagingConverter extends AbstractMessageConverter {
  /**
   * The events that are supported by this converter.
   */
  private static final Set<Class<?>> SUPPORTED_CLASSES = Set.of(
      UserBaseEvent.class,
      ApiKeyBaseEvent.class,
      RoleBaseEvent.class
  );
  
  private final ObjectMapper objectMapper;
  
  @PostConstruct
  public void init() {
    log.entry(objectMapper);
    
    log.debug("Registering supported classes: {}", SUPPORTED_CLASSES);
    
    super.addSupportedMimeTypes(MimeTypeUtils.APPLICATION_JSON);
    
    log.exit();
  }
  
  @Override
  protected boolean supports(final Class<?> clazz) {
    log.entry(clazz);
    
    boolean result = false;
    
    for (Class<?> supportedClass : SUPPORTED_CLASSES) {
      if (supportedClass.isAssignableFrom(clazz)) {
        result = true;
        break;
      }
    }
    
    return log.exit(result);
  }
  
  
  @Override
  protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
    log.entry(message, targetClass, conversionHint);
    
    String eventType = message.getHeaders().get("eventType", String.class);
    
    if (! eventType.equals(targetClass.getCanonicalName())) {
      throw log.throwing(
          new IllegalStateException("The target class and the the event type does not match! targetClass=%s, eventType=%s".formatted(targetClass.getCanonicalName(), eventType))
      );
    }
    log.debug("Working on message. eventType={}", eventType);
    
    return log.exit(objectMapper.convertValue(message.getPayload(), targetClass));
  }
  
  
  @Override
  protected String convertToInternal(
      final Object payload,
      @Nullable final MessageHeaders headers,
      @Nullable final Object conversionHint
  ) {
    log.entry(payload, headers, conversionHint);
    
    String result;
    try {
      result = objectMapper.writeValueAsString(payload);
      log.debug("Converted payload to JSON. json='{}'", result);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      
      result = null;
    }
    
    return log.exit(result);
  }
  
  public MessageHeaders headers(BaseEvent payload) {
    log.entry(payload);
    
    final var headers = new BaseEventMessageHeaders(
        Map.of(
            "eventType", payload.getClass().getCanonicalName(),
            MessageHeaders.CONTENT_TYPE, getSupportedMimeTypes().get(0)
        ),
        payload.getId(),
        payload.getTimestamp().toInstant().toEpochMilli()
    );
    
    return log.exit(headers);
  }
  
  /**
   * Need to override {@link MessageHeaders} to make the id and timestamp accessible.
   */
  private static class BaseEventMessageHeaders extends MessageHeaders {
    @Serial
    private static final long serialVersionUID = 1L;
    
    public BaseEventMessageHeaders(Map<String, Object> map, @Nullable UUID id, long timestamp) {
      super(map, id, timestamp);
    }
  }
}
