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


import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author klenkes74
 * @since 24.11.24
 */
@XSlf4j
public class LoggingEventBusTest {
  private final LoggingEventBus sut;

  public LoggingEventBusTest() {
    log.entry();

    sut = new LoggingEventBus();
    sut.register(this);

    log.exit();
  }

  @Test
  public void shouldRegisterWhenServiceIsNotRegistered() {
    log.entry();

    TestEvent event = new TestEvent(UUID.randomUUID());
    TestEventListener listener = new TestEventListener(event);

    // register
    sut.register(listener);

    // check registration
    sut.post(event);
    if (! listener.checkEventReceived()) {
      Assertions.fail("The registration failed. The event has not been received.");
    }

    // cleanup
    sut.unregister(listener);

    log.exit();
  }


  @Test
  public void shouldRegisterWhenServiceIsAlreadyRegistered() {
    log.entry();

    TestEvent event = new TestEvent(UUID.randomUUID());
    TestEventListener listener = new TestEventListener(event);

    // register
    sut.register(listener);
    sut.register(listener);

    // check registration
    sut.post(event);
    if (! listener.checkEventReceived()) {
      Assertions.fail("The registration failed. The event has not been received.");
    }

    // cleanup
    sut.unregister(listener);

    log.exit();
  }

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Map<UUID, TestEvent> events = new HashMap<>();
  @Subscribe
  public void receiveEvents(final TestEvent event) {
    log.entry(event);

    events.put(event.getId(), event);

    log.exit();
  }
}
