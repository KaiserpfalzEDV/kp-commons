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

package de.kaiserpfalzedv.commons.users.client.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.nio.file.AccessDeniedException;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthenticationITConfig.class})
public class AuthenticationIT {
  @Autowired
  private AuthenticationTestStubService service;
  
  @Test
  void shouldFailUserAllowedWhenNoUserIsSet() {
    StepVerifier
        .create(this.service.userAllowed())
        .expectError(AccessDeniedException.class)
        .verify();
  }
  
  @Test
  @WithMockUser
  void shouldFailUserAllowedWhenUserHasNoRole() {
    StepVerifier
        .create(this.service.userAllowed())
        .expectError(AccessDeniedException.class)
        .verify();
  }
  
  @Test
  @WithMockUser(roles = "USER")
  void shouldAcceptUserAllowedWhenUserHasUserRole() {
    StepVerifier
        .create(this.service.userAllowed())
        .expectNext("StepVerifier Subscriber")
        .verifyComplete();
  }
}
