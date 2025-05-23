package de.kaiserpfalzedv.commons.users.client.service;


import de.kaiserpfalzedv.commons.api.events.EventBus;
import de.kaiserpfalzedv.commons.users.domain.services.AuthenticationService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * The authentication manager for APIs.
 *
 * <p>This {@link AuthenticationManager} uses the APIKEY HTTP header to
 * authenticate the user and return the {@link de.kaiserpfalzedv.commons.users.domain.model.user.User} as
 * {@link Authentication}.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class ApiKeyAuthenticationManager implements AuthenticationManager {
  private final EventBus bus;
  private final AuthenticationService authenticationService;
  
  
  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    return null;
  }
}
