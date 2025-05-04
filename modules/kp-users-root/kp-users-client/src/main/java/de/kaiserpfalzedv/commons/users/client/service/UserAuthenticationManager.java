package de.kaiserpfalzedv.commons.users.client.service;


import de.kaiserpfalzedv.commons.core.events.LoggingEventBus;
import de.kaiserpfalzedv.commons.spring.security.KeycloakGroupAuthorityMapper;
import de.kaiserpfalzedv.commons.spring.security.KeycloakLogoutHandler;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
  private final LoggingEventBus bus;
  private final KpUserDetailsService userDetailsService;
  private final KeycloakGroupAuthorityMapper authorityMapper;
  private final KeycloakLogoutHandler logoutHandler;
  
  
  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    return null;
  }
}
