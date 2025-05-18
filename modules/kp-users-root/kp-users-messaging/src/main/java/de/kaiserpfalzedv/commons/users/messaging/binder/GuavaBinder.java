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

package de.kaiserpfalzedv.commons.users.messaging.binder;


import com.google.common.eventbus.Subscribe;
import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.activity.UserLoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-18
 */
@RequiredArgsConstructor
@ToString
@XSlf4j
public class GuavaBinder implements Binder<MessageChannel, ConsumerProperties, ProducerProperties> {
  private final LoggingEventBus bus;
  private final String application;
  
  @Override
  public Binding<MessageChannel> bindConsumer(final String name, final String group, final MessageChannel inboundBindTarget, final ConsumerProperties consumerProperties) {
    log.entry(name, group, inboundBindTarget, consumerProperties);
    

    GuavaSubscribedClass<?> subscribedClass;
    try {
      Class<?> clasz = UserLoginEvent.class;
          // getClass().getClassLoader().loadClass(consumerProperties.getSubscribeForClass());
      log.debug("Binding guava event bus subscriber. class={}", clasz);
      
      subscribedClass = (GuavaSubscribedClass) event -> {
        if (clasz.isAssignableFrom(event.getClass())) {
          log.debug("Received event. event={}", event);
          inboundBindTarget.send(new Message() {
            @Override
            public Object getPayload() {
              return event;
            }
            
            @Override
            public MessageHeaders getHeaders() {
              return new MessageHeaders(null);
            }
          });
          // bus.post(clasz.cast(event));
        }
      };
      
      log.info("Binding anonymous class for guave event bus. class={}, subscribedClass={}", clasz, subscribedClass);
    } catch (Exception e) {
      throw log.throwing(
          new IllegalStateException("Trying to subscribe for a class not found in classloader. message='%s', class='%s'".formatted(e.getMessage(), "clasz.getName()"))
      );
    }
    
    return log.exit(new Binding<MessageChannel>() {
      
      private final GuavaSubscribedClass<?> subscribed = subscribedClass;
      
      private Binding<MessageChannel> register() {
        bus.register(subscribed);
        
        return this;
      }
      
      private Binding<MessageChannel> unregister() {
        bus.unregister(subscribed);
        
        return this;
      }
      
      @Override
      public void start() {
        Binding.super.start();
        
        register();
      }
      
      @Override
      public void stop() {
        Binding.super.stop();
        
        unregister();
      }
      
      @Override
      public void pause() {
        Binding.super.pause();
        
        unregister();
      }
      
      @Override
      public void resume() {
        Binding.super.resume();
        
        register();
      }
      
      
      @Override
      public void unbind() {
        unregister();
      }
    }.register());
  }
  
  @Override
  public Binding<MessageChannel> bindProducer(final String name, final MessageChannel outboundBindTarget, final ProducerProperties producerProperties) {
    log.entry(name, outboundBindTarget, producerProperties);
    return log.exit(() -> log.info("Unbinding guava event bus producer. class={}", outboundBindTarget.getClass()));
  }
  
  private interface GuavaSubscribedClass<T> {
    @Subscribe
    void subscribe(T event);
  }
}
