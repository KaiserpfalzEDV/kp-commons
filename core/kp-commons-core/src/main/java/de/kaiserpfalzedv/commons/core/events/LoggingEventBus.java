/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.events;


import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import static org.slf4j.ext.XLogger.Level.INFO;


/**
 * EventBus for local observer pattern.
 *
 * <p>I understand that the EventBus authors advise not to use the EventBus.
 * But the reasons given there were considered and most of them are not considered disadvantages by me.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 24.11.24
 */
@Primary
@Service
@Order(1000)
@Scope("singleton")
@XSlf4j
public class LoggingEventBus extends EventBus {
  public LoggingEventBus() {
    log.entry(this);

    register(this);

    log.exit(this);
  }

  @SuppressWarnings("unused") // It is used by the EventBus
  @Subscribe
  public void deadLetterReporter(final DeadEvent event) {
    log.entry(event);

    log.error("Event has not been processed by any subscriber. event={}", event);

    log.exit(event);
  }


  @Override
  public void register(@NonNull final Object object) {
    log.entry(object);

    super.register(object);
    log.debug("Registered subscriber. subscriber={}", object);

    log.exit(object);
  }

  @Override
  public void unregister(@NonNull final Object object) {
    log.entry(object);

    try {
      super.unregister(object);
      log.debug("Unregistered subscriber. subscriber={}", object);
    } catch (IllegalArgumentException e) {
      log.catching(INFO, e);
    }

    log.exit(object);
  }

  @Override
  public void post(@NonNull final Object event) {
    log.entry(event);

    log.debug("Event distribution. event={}", event);
    super.post(event);

    log.exit(event);
  }
}
