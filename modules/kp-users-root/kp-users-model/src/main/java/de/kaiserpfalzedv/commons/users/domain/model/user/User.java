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

package de.kaiserpfalzedv.commons.users.domain.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;
import de.kaiserpfalzedv.commons.users.domain.model.abac.HasOwner;
import de.kaiserpfalzedv.commons.users.domain.model.user.state.UserState;
import jakarta.validation.constraints.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.security.Principal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The user of the DCIS application.
 */
@JsonDeserialize(as = KpUserDetails.class)
public interface User extends Principal, UserDetails, CredentialsContainer, HasId<UUID>, HasNameSpace, HasName, HasTimestamps, HasOwner<UUID>, Serializable {
  @Override
  default String getABACObjectName() {
    return "User";
  }
  
  /**
   * @return The IDP issuer of this user.
   */
  String getIssuer();
  
  /**
   * @return The IDP subject of this user.
   */
  String getSubject();
  
  /**
   * @return the email address of the user.
   */
  @Email
  String getEmail();
  
  /**
   * @return the phone number.
   */
  String getPhone();
  
  /**
   * @return the discord username.
   */
  String getDiscord();
  
  
  /**
   * @return The period the user has been detained for.
   */
  Duration getDetainmentDuration();
  
  /**
   * @return The end of the user detainment.
   */
  OffsetDateTime getDetainedTill();
  
  /**
   * @return The timestamp when the user has been banned.
   */
  OffsetDateTime getBannedOn();
  
  /**
   * Detains the user for a number of days.
   *
   * @param bus The bus for sending the changing event.
   * @param days The number of days the user is detained within the application.
   * @return the user
   */
  User detain(@NotNull EventBus bus, @Min(1) @Max(1095) long days);
  
  /**
   * Release the user from detainment.
   *
   * @param bus The bus for sending the changing event.
   * @return the user
   */
  User release(@NotNull EventBus bus);
  
  /**
   * Ban the user from the application.
   *
   * @param bus The bus for sending the changing event.
   * @return the user
   */
  User ban(@NotNull EventBus bus);
  
  /**
   * delete the user.
   *
   * @param bus The bus for sending the changing event.
   * @return  the user
   */
  User delete(@NotNull EventBus bus);
  
  /**
   * undelete the user
   *
   * @param bus The bus for sending the changing event.
   * @return the user
   */
  User undelete(@NotNull EventBus bus);
  
  
  /**
   * @return true if the user is neither banned, detained, nor deleted.
   */
  default boolean isActive() { return !isInactive(); }
  
  /**
   * @return true if the user is banned from the application.
   */
  default boolean isBanned() {
    return getBannedOn() != null;
  }
  
  
  /**
   * @return true if the user is detained.
   */
  default boolean isDetained() {
    return getDetainedTill() != null;
  }
  
  /**
   * @return true if the user is deleted.
   */
  default boolean isDeleted() {
    return getDeleted() != null;
  }
  
  /**
   * @return true if the user is inactive for any reason.
   */
  default boolean isInactive() {
    return (isDeleted() || isBanned() || isDetained());
  }
  
  
  @Override
  default UUID getOwnerId() {
    return getId();
  }

  @Override
  @JsonBackReference
  default HasId<UUID> getOwner() {
    return this;
  }
  
  default <R extends GrantedAuthority> boolean hasRole(@NotBlank final R role) {
    return getAuthorities().contains(role);
  }
  
  default boolean hasRole(@NotBlank final String role) {
    return getAuthorities().contains(new SimpleGrantedAuthority(role));
  }
  
  /**
   * Creates the user state from the user data itself.
   *
   * @param bus the event bus to be used during state changes.
   * @return the current user state.
   */
  default UserState getState(EventBus bus) {
    return UserState.Factory.fromUser(this, bus);
  }
  
  /**
   * Checks if the user is inactive.

   * @throws UserIsBannedException if the user is banned.
   * @throws UserIsDeletedException if the user is deleted.
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkInactive() throws UserIsBannedException, UserIsDeletedException, UserIsDetainedException {
    checkBanned();
    checkDeleted();
    checkDetained();
  }
  
  /**
   * Checks if the user is banned.
   *
   * @throws UserIsBannedException if the user is banned
   */
  default void checkBanned() throws UserIsBannedException {
    if (isBanned()) {
      throw new UserIsBannedException(this);
    }
  }
  
  /**
   * Checks if the user is deleted.
   *
   * @throws UserIsDeletedException if the user is deleted.
   */
  default void checkDeleted() throws UserIsDeletedException {
    if (isDeleted() && !isBanned()) {
      throw new UserIsDeletedException(this);
    }
  }
  
  /**
   * Checks if the user is detained.
   *
   * @throws UserIsDetainedException if the user is detained.
   */
  default void checkDetained() throws UserIsDetainedException {
    if (isDetained()) {
      throw new UserIsDetainedException(this);
    }
  }
  
  
  @Override
  @JsonIgnore
  default String getPassword() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  @JsonIgnore
  default String getUsername() {
    return getName();
  }
  
}