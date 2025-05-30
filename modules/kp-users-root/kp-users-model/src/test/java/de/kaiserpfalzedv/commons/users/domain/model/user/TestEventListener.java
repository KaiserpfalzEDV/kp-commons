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

package de.kaiserpfalzedv.commons.users.domain.model.user;


import de.kaiserpfalzedv.commons.users.domain.model.user.events.arbitration.UserPetitionedEvent;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.*;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;


/**
 * 
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since  2025-05-23
 */
@Service
@Getter
@ToString
@XSlf4j
public class TestEventListener {
  public void clear() {
    log.entry();
    
    this.releasedUser = null;
    this.detainedUser = null;
    this.activeUser = null;
    this.bannedUser = null;
    this.deletedUser = null;
    this.removedUser = null;
    this.petitionUser = null;
    
    log.exit();
  }
  
  
  private UserReleasedEvent releasedUser = null;
  @org.springframework.context.event.EventListener
  public void onReleasedUser(UserReleasedEvent event) {
    log.entry(event);
    this.releasedUser = event;
    log.exit(event);
  }
  
  private UserDetainedEvent detainedUser = null;
  @org.springframework.context.event.EventListener
  public void onDetainedUser(UserDetainedEvent detainedUser) {
    log.entry(detainedUser);
    this.detainedUser = detainedUser;
    log.exit(detainedUser);
  }
  
  private UserActivatedEvent activeUser = null;
  @org.springframework.context.event.EventListener
  public void onActiveUser(UserActivatedEvent activeUser) {
    log.entry(activeUser);
    this.activeUser = activeUser;
    log.exit(activeUser);
  }
  
  private UserBannedEvent bannedUser = null;
  @org.springframework.context.event.EventListener
  public void onBannedUser(UserBannedEvent bannedUser) {
    log.entry(bannedUser);
    this.bannedUser = bannedUser;
    log.exit(bannedUser);
  }
  
  private UserDeletedEvent deletedUser = null;
  @org.springframework.context.event.EventListener
  public void onDeletedUser(UserDeletedEvent deletedUser) {
    log.entry(deletedUser);
    this.deletedUser = deletedUser;
    log.exit(deletedUser);
  }
  
  private UserRemovedEvent removedUser = null;
  @org.springframework.context.event.EventListener
  public void onRemovedUser(UserRemovedEvent event) {
    log.entry(event);
    removedUser = event;
    log.exit(event);
  }
  
  
  private UserPetitionedEvent petitionUser = null;
  @org.springframework.context.event.EventListener
  public void onPetitionedUser(UserPetitionedEvent event) {
    log.entry(event);
    petitionUser = event;
    log.exit(event);
  }
}
