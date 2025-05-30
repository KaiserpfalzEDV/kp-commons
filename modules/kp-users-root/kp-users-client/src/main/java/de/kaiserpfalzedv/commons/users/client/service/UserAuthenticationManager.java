package de.kaiserpfalzedv.commons.users.client.service;


import de.kaiserpfalzedv.commons.users.client.model.KpUserAuthentication;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsInactiveException;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.AuthenticationService;
import de.kaiserpfalzedv.commons.users.domain.services.UserAuthenticationException;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * The authentication manager for user interactions.
 *
 * <p>This {@link AuthenticationManager} uses the {@link org.springframework.security.oauth2.core.oidc.OidcIdToken} to
 * authenticate the user and return the {@link de.kaiserpfalzedv.commons.users.domain.model.user.User} as
 * {@link Authentication}.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class UserAuthenticationManager implements AuthenticationManager {
  private final AuthenticationService authenticationService;
  
  
  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    log.entry(authentication);
    
    KpUserAuthentication result;
    
    if (! (authentication.getPrincipal() instanceof OidcUser oidcUser)) {
      log.debug("Working only on authentication of type 'OidcUser'. type={}", authentication.getClass().getSimpleName());
      
      return log.exit(authentication);
    }
    
    try {
      User user = authenticationService.authenticate(oidcUser);
      result = KpUserAuthentication.builder().principal(user).build();
    } catch (UserIsInactiveException | UserCantBeCreatedException e) {
      throw new UserAuthenticationException(e);
    }
    
    return log.exit(result);
  }
}
