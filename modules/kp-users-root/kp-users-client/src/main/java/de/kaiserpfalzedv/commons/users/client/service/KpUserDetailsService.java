package de.kaiserpfalzedv.commons.users.client.service;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class KpUserDetailsService implements UserDetailsService {
  private final UserReadService read;
  
  /**
   *
   * @param username the username identifying the user whose data is required. It has to be the provider joined by ":#:" with the subject of the OAuth2 token.
   * @return The details for the user.
   * @throws UsernameNotFoundException If the username can't be devided to issuer and subject or the user is not found in our database.
   */
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.entry(username);
    
    String[] credentials = getCredentialsOrThrowException(username);
    
    return log.exit(loadUserOrThrowException(username, credentials));
  }
  
  private static String[] getCredentialsOrThrowException(final String username) {
    log.entry(username);
    
    if (username == null || !username.contains(":#:")) {
      throw log.throwing(new UsernameNotFoundException(username));
    }
    
    String[] credentials = username.split(":#:", 2);
    if (credentials.length != 2) {
      throw log.throwing(new UsernameNotFoundException(username));
    }
    
    return log.exit(credentials);
  }
  
  private User loadUserOrThrowException(final String username, final String[] credentials) {
    log.entry(username, credentials);
    
    Optional<? extends User> user = read.findByOauth(credentials[0], credentials[1]);
    
    if (user.isEmpty()) {
      throw log.throwing(new UsernameNotFoundException(username));
    }
    
    return log.exit(user.get());
  }
}
