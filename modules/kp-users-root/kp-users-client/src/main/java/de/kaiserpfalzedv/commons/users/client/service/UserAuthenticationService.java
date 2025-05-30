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

package de.kaiserpfalzedv.commons.users.client.service;


import de.kaiserpfalzedv.commons.users.domain.model.user.*;
import de.kaiserpfalzedv.commons.users.domain.services.AuthenticationService;
import de.kaiserpfalzedv.commons.users.domain.services.UserManagementService;
import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserAuthenticationService implements AuthenticationService {
  private final UserReadService<User> readService;
  private final UserManagementService writeService;
  
  @Value("${spring.application.system:no-system")
  private String namespace = "no-system";
  
  
  @Override
  public User authenticate(final Authentication authentication) throws UserIsInactiveException, UserCantBeCreatedException {
    log.entry(namespace, authentication);
    
    if (authentication.getPrincipal() instanceof OidcUser) {
      return log.exit(authenticate((OidcUser) authentication.getPrincipal()));
    }
    
    return log.exit(null);
  }
  
  @Override
  public User authenticate(final OidcUser oidcUser) throws UserIsInactiveException, UserCantBeCreatedException {
    log.entry(namespace, oidcUser);
    
    Optional<User> data = readService.findByOauth(oidcUser.getIssuer().toString(), oidcUser.getSubject());
    
    log.debug("Data found. found={}, data={}", data.isPresent(), data.orElse(null));
    
    User result = data.orElse(null);
    if (result == null) {
      result = createNewUser(oidcUser);
    }
    
    checkForInactiveUsers(result);
    
    return log.exit(result);
  }
  
  
  private User createNewUser(final OidcUser oidcUser) throws UserCantBeCreatedException {
    log.entry(oidcUser);
    
    User user = createUserFromOidcUser(oidcUser);
    writeService.create(user);

    return log.exit(user);
  }
  
  
  private User createUserFromOidcUser(final OidcUser oidcUser) {
    log.entry(oidcUser);
    
    UUID id = readIdFromSubjectId(oidcUser);
    
    return log.exit(KpUserDetails.builder()
            .id(id)
        
            .issuer(oidcUser.getIssuer().toString())
            .subject(oidcUser.getSubject())
            
            .nameSpace(namespace)
            .name(oidcUser.getPreferredUsername())
            
            .email(oidcUser.getEmail())
            .phone(oidcUser.getPhoneNumber())
            
            .build());
  }
  
  private static UUID readIdFromSubjectId(final OidcUser oidcUser) {
    log.entry(oidcUser);
    
    UUID result;
    
    try {
      result = UUID.fromString(oidcUser.getSubject());
    } catch (IllegalArgumentException e) {
      result = UUID.randomUUID();
    }
    
    return log.exit(result);
  }
  
  
  private static void checkForInactiveUsers(final User user) throws UserIsDeletedException, UserIsBannedException, UserIsDetainedException {
    log.entry(user);
    
    if (user.isInactive()) {
      checkUserIsDeleted(user);
      checkUserIsBanned(user);
      checkUserIsDetained(user);
    }
    
    log.exit();
  }
  
  private static void checkUserIsDeleted(final User user) throws UserIsDeletedException {
    if (user.isDeleted()) {
      throw log.throwing(new UserIsDeletedException(user));
    }
  }
  
  private static void checkUserIsBanned(final User user) throws UserIsBannedException {
    if (user.isBanned()) {
      throw log.throwing(new UserIsBannedException(user));
    }
  }
  
  private static void checkUserIsDetained(final User user) throws UserIsDetainedException {
    if (user.isDetained()) {
      throw log.throwing(new UserIsDetainedException(user));
    }
  }
}
