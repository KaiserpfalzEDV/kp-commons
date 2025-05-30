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
import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 12.04.25
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString(of = {"user"})
@XSlf4j
public class DetainedUser implements UserState {
  @Getter
  final private User user;
  final private EventBus bus;
  
  @Override
  public UserState activate() {
    return this;
  }
  
  @Override
  public UserState detain(final long days) {
    user.detain(bus, days);
    
    return DetainedUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState release() {
    user.release(bus);
    
    if (user.isDeleted()) {
      return DeletedUser.builder().user(user).bus(bus).build();
    }

    return ActiveUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState ban() {
    user.ban(bus);
    
    return BannedUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState delete() {
    user.delete(bus);
    
    return DeletedUser.builder().user(user).bus(bus).build();
  }
  
  
  @Override
  public UserState remove(final boolean delete) {
    user.delete(bus);
    
    log.warn("Will not delete user since it is detained. It has to be removed after the detention is ended. detainedTill={}", user.getDetainedTill());
    
    return DeletedUser.builder().user(user).bus(bus).build();
  }
  
  @Override
  public UserState petition(final UUID petition) {
    bus.post(UserPetitionedEvent.builder().user(user).petition(petition).build());
    
    return this;
  }
}
