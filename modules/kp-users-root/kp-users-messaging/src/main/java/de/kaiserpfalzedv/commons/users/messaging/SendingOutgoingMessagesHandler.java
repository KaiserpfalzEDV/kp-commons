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


import com.google.common.eventbus.Subscribe;
import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.UserBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserActivityBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserArbitrationBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.modification.UserModificationBaseEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserStateBaseEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Handles user events and sends them to the appropriate topic.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@Service
@Scope("singleton")
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class SendingOutgoingMessagesHandler implements AutoCloseable {
  private final StreamBridge bridge;
  private final LoggingEventBus bus;
  
  @Value("${spring.application.name:kp-users}")
  private String application = "kp-users";
  
  @Value("${spring.application.system:kp-commons}")
  private String system = "kp-commons";

  @Value("${kp-users.messaging.topic.names.activity:activity}")
  private String activityTopic = "activity";
  @Value("${kp-users.messaging.topic.names.arbitration:arbitration}")
  private String arbitrationTopic = "arbitration";
  @Value("${kp-users.messaging.topic.names.modification:modification}")
  private String modificationTopic = "modification";
  @Value("${kp-users.messaging.topic.names.state:state}")
  private String stateTopic = "state";

  
  @PostConstruct
  public void init() {
    log.entry(bus, system);
    
    bus.register(this);
    
    log.exit();
  }
  
  @PreDestroy
  public void close() {
    log.entry(bus, system);
    
    bus.unregister(this);
    
    log.exit();
  }
  
  
  @Subscribe
  public void event(@NotNull final UserBaseEvent event) {
    log.entry(event);
    
    if (isInternalEvent(event)) {
      send(selectTopic(event), event);
    }
    
    log.exit();
  }
  
  
  private boolean isInternalEvent(@NotNull @NotNull final UserBaseEvent event) {
    log.entry(application, event);
    
    return log.exit(application.equals(event.getApplication()));
  }

  /**
   * Selects the topic for the given event.
   *
   * @param event The event to select the topic for.
   * @return The topic for the event.
   */
  private String selectTopic(@NotNull final UserBaseEvent event) {
    log.entry(event);
    
    // TODO 2025-05-18 klenkes74 Find a nicer way than a list of if statements to select the correct topic.
    
    String result;
    
    if (UserArbitrationBaseEvent.class.isAssignableFrom(event.getClass())) {
      result = arbitrationTopic;
    } else if (UserModificationBaseEvent.class.isAssignableFrom(event.getClass())) {
      result = modificationTopic;
    } else if (UserStateBaseEvent.class.isAssignableFrom(event.getClass())) {
      result = stateTopic;
    } else if (UserActivityBaseEvent.class.isAssignableFrom(event.getClass())) {
      result = activityTopic;
    } else { // In case of an unknown event, we return the activity topic.
      log.error("Unknown event type. event={}, default='{}'", event, activityTopic);
      result = activityTopic;
    }
    
    
    return log.exit(result);
  }

  /**
   * Sends the event to the given topic.
   *
   * @param topic The topic to send the event to.
   * @param event The event to send.
   */
  private void send(@NotNull final String topic, @NotNull final UserBaseEvent event) {
    log.entry(topic, event);
    
    bridge.send(constructTopicName(topic), event);
    
    log.exit();
  }
  
  private String constructTopicName(@NotNull final String topic) {
    log.entry(system, topic);
    
    return log.exit("%s.%s".formatted(system, topic));
  }
}
