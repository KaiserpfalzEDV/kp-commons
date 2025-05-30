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


import de.kaiserpfalzedv.commons.users.client.model.KpUserAuthentication;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsBannedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.AuthenticationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class UserAuthenticationManagerTest {
  /** Service under test */
  private UserAuthenticationManager sut;
  
  @Mock
  private AuthenticationService authenticationService;
  
  @Mock
  private Authentication authentication;
  @Mock
  private OidcUser oidcUser;

  @Mock
  private OneTimeTokenAuthenticationToken oneTimeToken;
  
  @BeforeEach
  public void setUpTest() {
    sut = new UserAuthenticationManager(authenticationService);
  }

  
  @SneakyThrows
  @Test
  public void shouldReturnAnKpUserAuthenticationWhenCalledWithOidcUser() {
    log.entry();
    
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(authenticationService.authenticate(oidcUser)).thenReturn(PLAYER);
    
    Authentication result = sut.authenticate(authentication);
    
    assertInstanceOf(KpUserAuthentication.class, result);
    assertEquals(PLAYER, result.getPrincipal());
    
    log.exit(result);
  }

  
  @SneakyThrows
  @Test
  public void shouldThrowAnExceptionWhenUserIsInactive() {
    log.entry();
    
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(authenticationService.authenticate(oidcUser)).thenThrow(
        new UserIsBannedException(((KpUserDetails)PLAYER).toBuilder().bannedOn(OffsetDateTime.now().minusDays(5L)).build())
    );
    
    try {
      sut.authenticate(authentication);
      
      Assertions.fail("There should have been an AuthenticationException!");
    } catch (AuthenticationException e) {
      log.debug("The correct exception has been thrown.", e);
    }
    
    log.exit();
  }
  
  
  @SneakyThrows
  @Test
  public void shouldThrowAnExceptionWhenUserCantBeCreated() {
    log.entry();
    
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(authenticationService.authenticate(oidcUser)).thenThrow(new UserCantBeCreatedException(DEFAULT_ISSUER.toString(), PLAYER.getSubject(), PLAYER.getUsername(), PLAYER.getEmail()));
    
    try {
      sut.authenticate(authentication);
      
      Assertions.fail("There should have been an AuthenticationException!");
    } catch (AuthenticationException e) {
      log.debug("The correct exception has been thrown.", e);
    }
    
    log.exit();
  }
  
  @Test
  public void shouldReturnUnchangedAuthenticationWhenNotOfTypeOidcUser() {
    log.entry();
    
    when(authentication.getPrincipal()).thenReturn(oneTimeToken);
    
    Authentication result = sut.authenticate(authentication);
    
    assertEquals(authentication, result);
    log.exit(result);
  }
  
  
  private static final URL DEFAULT_ISSUER;
  static {
    try {
      DEFAULT_ISSUER = new URL("https://sso.delphi-council.org/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
  private static final UUID SUBJECT = UUID.randomUUID();
  
  private static final String NAMESPACE = "urn:kp-user-root";
  public static final String NAME_PLAYER = "Patricia Player";
  private static final String EMAIL = "user@urn.kp-user-root";
  private static final String PHONE = "+49 1234-12345678";
  private static final String DISCORD = "user";
  
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now().minusMonths(6L);
  private static final OffsetDateTime MODIFIED_AT = OffsetDateTime.now().minusMonths(1L);
  
  private static final Set<SimpleGrantedAuthority> PLAYER_AUTHORITIES = Set.of(
      new SimpleGrantedAuthority("ROLE_PLAYER")
  );
  
  
  private static final User PLAYER = KpUserDetails.builder()
      .issuer(DEFAULT_ISSUER.toString())
      .subject(SUBJECT.toString())
      .nameSpace(NAMESPACE)
      .name(NAME_PLAYER)
      .email(EMAIL)
      .phone(PHONE)
      .discord(DISCORD)
      .created(CREATED_AT)
      .modified(MODIFIED_AT)
      .authorities(PLAYER_AUTHORITIES)
      .build();
}
