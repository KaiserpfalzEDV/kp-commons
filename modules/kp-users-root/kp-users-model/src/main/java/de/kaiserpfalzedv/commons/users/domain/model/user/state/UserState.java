/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.kaiserpfalzedv.commons.users.domain.model.user.state;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


/**
 * This is the state machine for user.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 12.04.25
 */

public interface UserState {
  class Factory {
    static public UserState fromUser(@NotNull final User user, @NotNull final EventBus bus) {
      if  (user.isActive()) {
        return ActiveUser.builder()
            .user(user)
            .bus(bus)
            .build();
      } else if (user.isBanned()) {
        return BannedUser.builder()
            .user(user)
            .bus(bus)
            .build();
      } else if (user.isDetained()) {
        return DetainedUser.builder()
            .user(user)
            .bus(bus)
            .build();
      } else {
        // If the user is not active, banned, nor detained. It has to be deleted!
        
        return DeletedUser.builder()
            .user(user)
            .bus(bus)
            .build();
      }
    }
  }
  
  
  User getUser();
  
  /**
   * A petition concerning this user has been created.
   * @param petition The ID of the created pertition.
   * @return the current user state.
   */
  UserState petition(final UUID petition);
  
  default boolean isActive() {
    return getUser().isActive();
  }
  
  default boolean isInactive() {
    return getUser().isInactive();
  }
  
  default boolean isDeleted() {
    return getUser().isDeleted();
  }
  
  default boolean isBanned() {
    return getUser().isBanned();
  }
  
  default boolean isDetained() {
    return getUser().isDetained();
  }
  
  /**
   * The user is detained for the given number of days.
   *
   * @param days The number of days the user is to be detained. Starting from 00h00 the following day.
   * @return the new state of the user.
   */
  UserState detain(final long days);
  
  /**
   * Bans the user from the application.
   *
   * @return the new state of the user.
   */
  UserState ban();
  
  /**
   * Releases the user from bans and detentions.
   *
   * @return the new state of the user.
   */
  UserState release();
  
  /**
   * Deletes the user from the application. Deletion means the user is inactive (can't use the application any more). The data will stay in the
   * application for the data retention period.
   *
   * @return the new state of the user.
   */
  UserState delete();
  
  /**
   * Activates a deleted user again.
   *
   * @return the new state of the user.
   */
  UserState activate();
  
  /**
   * Removes the user from the application. The user will not be able to be activated again.
   *
   * @param delete If the user data should be deleted. Otherwise, the data will be anonymized but stay in the application.
   * @return the new state of the user.
   */
  UserState remove(final boolean delete);
}
