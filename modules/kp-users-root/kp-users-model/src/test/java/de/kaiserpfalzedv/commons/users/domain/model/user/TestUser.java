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


import de.kaiserpfalzedv.commons.api.events.EventBus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


/**
 * The test implementation for User.
 * Since the KPUserDetails may overwrite stuff we need a clean implementation that does not override the default methods.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "nameSpace", "name"})
@XSlf4j
public class TestUser implements User {
  @Builder.Default
  private UUID id = UUID.randomUUID();
  
  @Builder.Default
  private String issuer = "issuer";
  
  @Override
  public String getSubject() {
    return id.toString();
  }
  
  @Builder.Default
  private String nameSpace = "namespace";
  @Builder.Default
  private String name = "name";
  
  @Builder.Default
  private String email = "email@email.email";
  @Builder.Default
  private String phone = "123456789";
  @Builder.Default
  private String discord = "discord";
  
  private OffsetDateTime deleted;
  
  @Override
  public Duration getDetainmentDuration() {
    return null;
  }
  
  @Override
  public OffsetDateTime getDetainedTill() {
    return null;
  }
  
  private OffsetDateTime bannedOn;
  
  @Override
  public User detain(final EventBus bus, final long days) {
    return null;
  }
  
  @Override
  public User release(final EventBus bus) {
    return null;
  }
  
  @Override
  public User ban(final EventBus bus) {
    bannedOn = OffsetDateTime.now();
    log.trace("User got banned. bannedOn={}", bannedOn);
    
    return this;
  }
  
  @Override
  public User delete(final EventBus bus) {
    deleted = OffsetDateTime.now();
    log.trace("User got deleted. deleted={}", deleted);
    
    return this;
  }
  
  @Override
  public User undelete(final EventBus bus) {
    return null;
  }
  
  @Override
  public OffsetDateTime getCreated() {
    return null;
  }
  
  @Override
  public OffsetDateTime getModified() {
    return null;
  }
  
  @Override
  public void eraseCredentials() {
  
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }
}