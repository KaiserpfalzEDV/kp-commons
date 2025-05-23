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

package de.kaiserpfalzedv.commons.api.events;


import jakarta.validation.constraints.NotNull;

/**
 * EventBus for local observer pattern.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-23
 */
public interface EventBus {
  /**
   * Register an listener to receive events.
   *
   * @param listener the listener to register
   */
  void register(@NotNull Object listener);
  
  /**
   * Unregister an listener to stop receiving events.
   *
   * @param listener the listener to unregister
   */
  void unregister(@NotNull Object listener);
  
  /**
   * Post an event to the bus.
   *
   * @param event the event to post
   */
  void post(@NotNull Object event);
  
  /**
   * Post an event to the bus.
   *
   * @param event the event to post
   */
  default void publishEvent(@NotNull Object event) {
    post(event);
  }
}
