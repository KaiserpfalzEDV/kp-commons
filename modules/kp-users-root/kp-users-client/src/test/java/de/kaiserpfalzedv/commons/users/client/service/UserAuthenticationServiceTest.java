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


import de.kaiserpfalzedv.commons.users.domain.model.user.BaseUserException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsBannedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsDeletedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsDetainedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserIsInactiveException;
import de.kaiserpfalzedv.commons.users.domain.model.apikey.InvalidApiKeyException;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserReadService;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserWriteService;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class UserAuthenticationServiceTest {
  /** Service under test */
  private UserAuthenticationService sut;
  
  @Mock
  private UserReadService userReadService;
  
  @Mock
  private UserWriteService userWriteService;
  
  @Mock
  private Authentication authentication;
  
  @Mock
  private OidcUser oidcUser;
  
  @BeforeEach
  public void setUpTest() {
    sut = new UserAuthenticationService(userReadService, userWriteService);
  }

  
  @Test
  public void shouldReturnAnExistingUser() throws UserIsInactiveException, UserCantBeCreatedException {
    log.entry();

    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(PLAYER.getSubject());
    when(userReadService.findByOauth(PLAYER.getIssuer(), PLAYER.getSubject())).thenReturn(Optional.of(PLAYER));

    User result = sut.authenticate(oidcUser);
    
    assertEquals(PLAYER, result);
    
    log.exit(result);
  }
  
  @Test
  public void shouldReturnAnExistingUserWhenCallingWithOidcUserBasedAuthentication() throws UserIsInactiveException, UserCantBeCreatedException, InvalidApiKeyException {
    log.entry();
    
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(PLAYER.getSubject());
    when(userReadService.findByOauth(PLAYER.getIssuer(), PLAYER.getSubject())).thenReturn(PLAYER_OPTIONAL);
    
    User result = sut.authenticate(authentication);
    
    assertEquals(PLAYER, result);
    
    log.exit(result);
  }
  
  
  @Test
  public void shouldThrowAnExceptionWhenTheUserIsBanned() throws BaseUserException {
    log.entry();
    
    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(BANNED.getSubject());
    when(userReadService.findByOauth(BANNED.getIssuer(), BANNED.getSubject())).thenReturn(Optional.of(BANNED));
    
    try {
      sut.authenticate(oidcUser);
      
      fail("There should have been an UserIsBannedException.");
    } catch (UserIsBannedException e) {
      // everything is fine.
      log.exit(e);
    } catch (UserCantBeCreatedException | UserIsDetainedException | UserIsDeletedException e) {
      throw log.throwing(e);
    }
  }
  
  
  @Test
  public void shouldThrowAnExceptionWhenTheUserIsDetained() throws BaseUserException {
    log.entry();
    
    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(DETAINED.getSubject());
    when(userReadService.findByOauth(DETAINED.getIssuer(), DETAINED.getSubject())).thenReturn(Optional.of(DETAINED));
    
    try {
      sut.authenticate(oidcUser);
      
      fail("There should have been an UserIsDetainedException.");
    } catch (UserIsDetainedException e) {
      // everything is fine.
      log.exit(e);
    } catch (UserCantBeCreatedException | UserIsBannedException | UserIsDeletedException e) {
      throw log.throwing(e);
    }
  }
  
  
  @Test
  public void shouldThrowAnExceptionWhenTheUserIsDeleted() throws BaseUserException {
    log.entry();
    
    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(DELETED.getSubject());
    when(userReadService.findByOauth(DELETED.getIssuer(), DELETED.getSubject())).thenReturn(Optional.of(DELETED));
    
    try {
      sut.authenticate(oidcUser);
      
      fail("There should have been an UserIsDeletedException.");
    } catch (UserIsDeletedException e) {
      // everything is fine.
      log.exit(e);
    } catch (UserCantBeCreatedException | UserIsBannedException | UserIsDetainedException e) {
      throw log.throwing(e);
    }
  }
  
  
  @Test
  public void shouldCreateUserIfItDoesNotExist() throws BaseUserException {
    log.entry();
    
    when(oidcUser.getIssuer()).thenReturn(DEFAULT_ISSUER);
    when(oidcUser.getSubject()).thenReturn(CREATED.getId().toString());
    when(oidcUser.getPreferredUsername()).thenReturn(CREATED.getUsername());
    
    when(userReadService.findByOauth(CREATED.getIssuer(), CREATED.getSubject())).thenReturn(Optional.empty());
    
    verify(userWriteService).create(CREATED);
    
    User result = sut.authenticate(oidcUser);
    
    assertEquals(CREATED, result);
    
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
  public static final String NAME_BANNED = "Berta Banned";
  public static final String NAME_DETAINED = "Diana Detained";
  public static final String NAME_DELETED = "Drue Deleted";
  public static final String NAME_CREATED = "Clara Created";
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
      .id(SUBJECT)
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
  private static final Optional<? extends User> PLAYER_OPTIONAL = Optional.of(PLAYER);
  
  private static final User BANNED = ((KpUserDetails)PLAYER).toBuilder()
      .name(NAME_BANNED)
      .bannedOn(PLAYER.getModified())
      .build();
  private static final User DETAINED = ((KpUserDetails)PLAYER).toBuilder()
      .name(NAME_DETAINED)
      .detainedTill(OffsetDateTime.now().plusDays(6L))
      .detainmentDuration(Duration.ofDays(90L))
      .build();
  private static final User DELETED = ((KpUserDetails)PLAYER).toBuilder()
      .name(NAME_DELETED)
      .deleted(PLAYER.getModified())
      .build();
  
  private static final User CREATED = ((KpUserDetails)PLAYER).toBuilder()
      .name(NAME_CREATED)
      .build();
}
