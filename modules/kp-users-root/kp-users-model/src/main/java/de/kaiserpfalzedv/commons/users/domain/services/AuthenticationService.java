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

package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsInactiveException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * The service to authenticate user either via OpenIDConnect or via api key.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
public interface AuthenticationService {
  /**
   * Loads the authentication data from the underlying store.
   *
   * <p>If the user is (1) authenticated via OpenIDConnect and (2) does not exist in the store, it will be created with
   * the <em>preferredUsername</em> of the JWT. When the user is created an event will be sent via messaging for the
   * other systems.</p>
   *
   * <p>When the authentication is done via APIKEY, it will load the user referenced by the
   * {@link de.kaiserpfalzedv.commons.users.domain.model.apikey.ApiKey}. Any problem (e.g. the user is inactive) will
   * be hidden behind the {@link InvalidApiKeyException}.</p>
   *
   * @param authentication The authentication created by spring-security.
   * @return The user authenticated or created in the database.
   * @throws UserIsInactiveException if the user is deleted, detained, or banned.
   * @throws UserCantBeCreatedException if the user does not exist and can't be created.
   * @throws InvalidApiKeyException if the APIKEY is not valid for any reason.
   */
  User authenticate(@NotNull Authentication authentication) throws UserIsInactiveException, UserCantBeCreatedException, InvalidApiKeyException;
  
  User authenticate(@NotNull OidcUser oidcUser) throws UserIsInactiveException, UserCantBeCreatedException;
}
