/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.api.resources.*;
import de.kaiserpfalzedv.commons.users.domain.model.abac.HasOwner;
import de.kaiserpfalzedv.commons.users.domain.model.abac.OwnedBy;
import de.kaiserpfalzedv.commons.users.domain.model.user.KpUserDetails;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 01.05.2025
 */
@XSlf4j
class OwnedByTest {
  private static final User peter = KpUserDetails.builder()
      .issuer("https://sso.kaiserpfalz-edv.de/realms/Paladins-Inn")
      .subject(UUID.randomUUID().toString())
      .name("Peter")
      .nameSpace("Torganized Play")
      .authorities(Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_PLAYER")))
      .build();
  private static final User paul = KpUserDetails.builder()
      .issuer("https://sso.kaiserpfalz-edv.de/realms/Paladins-Inn")
      .subject(UUID.randomUUID().toString())
      .name("Paul")
      .nameSpace("Torganized Play")
      .authorities(Set.of(new SimpleGrantedAuthority("ROLE_ORGA"), new SimpleGrantedAuthority("ROLE_PLAYER")))
      .build();
  private static final User mary = KpUserDetails.builder()
      .issuer("https://sso.kaiserpfalz-edv.de/realms/Paladins-Inn")
      .subject(UUID.randomUUID().toString())
      .name("Mary")
      .nameSpace("Torganized Play")
      .authorities(Set.of(new SimpleGrantedAuthority("ROLE_GM"), new SimpleGrantedAuthority("ROLE_PLAYER")))
      .build();
  private static final User andrew = KpUserDetails.builder()
      .issuer("https://sso.kaiserpfalz-edv.de/realms/Paladins-Inn")
      .subject(UUID.randomUUID().toString())
      .name("Andrew")
      .nameSpace("Torganized Play")
      .authorities(Set.of(new SimpleGrantedAuthority("ROLE_PLAYER")))
      .build();
  
  private static final TestObject MarysObject = TestObject.builder()
      .nameSpace("Villains World")
      .name("Marys Object")
      .owner(mary)
      .build();
  
  private static Enforcer sut;
  
  
  @BeforeAll
  public static void setUp() {
    log.entry();
    
    sut = new Enforcer("target/test-classes/abac/model.conf", "target/test-classes/abac/model.csv");
    sut.addFunction("ownedBy", new OwnedBy());
    
    log.exit(sut);
  }
  
  
  @Test
  void shouldAllowChangingMarysObjectForPeter() {
    assertTrue(sut.enforce(peter, MarysObject, "update"));
  }
  
  @Test
  void shouldAllowChangingMarysObjectForPaul() {
    assertTrue(sut.enforce(paul, MarysObject, "update"));
  }
  
  
  @Test
  void shouldAllowChangingMarysObjectForMary() {
    assertTrue(sut.enforce(mary, MarysObject, "update"));
  }
  
  @Test
  void shouldNotAllowChangingMarysObjectForAndrew() {
    assertFalse(sut.enforce(andrew, MarysObject, "update"));
  }
  
  
  @Test
  void shouldAllowDeletingMarysObjectForMary() {
    assertTrue(sut.enforce(mary, MarysObject, "delete"));
  }
  
  @Test
  void shouldNotAllowRemovingMarysObjectForPaul() {
    assertFalse(sut.enforce(paul, MarysObject, "remove"));
  }
  
  
  @Test
  void shouldAllowRemovingMarysObjectForPeter() {
    assertTrue(sut.enforce(peter, MarysObject, "remove"));
  }
  
  
  @Test
  void shouldAllowDeletingAndrewForAndrew() {
    assertTrue(sut.enforce(andrew, andrew, "delete"));
  }
  
  
  @Test
  void shouldNotAllowDeletingAndrewForMary() {
    assertFalse(sut.enforce(mary, andrew, "delete"));
  }
  
  
  @Test
  void shouldAllowDeletingAndrewForPaul() {
    assertTrue(sut.enforce(paul, andrew, "delete"));
  }
  
  
  @Builder(toBuilder = true)
  @Getter
  @ToString
  @EqualsAndHashCode(of = {"id"})
  private static class TestObject implements Serializable, HasId<UUID>, HasNameSpace, HasName, HasDisplayName, HasTimestamps, HasOwner<UUID> {
    @NotNull
    @Builder.Default
    private final UUID id = UUID.randomUUID();
    
    @NotBlank
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.")
    @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    private final String nameSpace;

    @NotBlank
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.")
    @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    private final String name;
    
    @NotNull
    @Builder.Default
    private final OffsetDateTime created = OffsetDateTime.now();
    
    @NotNull
    @Builder.Default
    private final OffsetDateTime modified = OffsetDateTime.now();

    @Nullable
    private final OffsetDateTime deleted;
    
    @NotNull
    private final User owner;
  }
}
