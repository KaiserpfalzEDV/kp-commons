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

package de.kaiserpfalzedv.commons.users.client.model;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString
public class KpUserAuthentication implements Authentication {
  private final User principal;
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return principal.getAuthorities();
  }
  
  @Override
  public Object getCredentials() {
    return principal.getPassword();
  }
  
  @Override
  public User getDetails() {
    return principal;
  }
  
  @Override
  public String getName() {
    return principal.getName();
  }
  
  @Override
  public boolean isAuthenticated() {
    return true;
  }
  
  @Override
  public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
    if (! isAuthenticated) {
      throw new IllegalArgumentException("This authentication can't be unauthenticated");
    }
  }
}
