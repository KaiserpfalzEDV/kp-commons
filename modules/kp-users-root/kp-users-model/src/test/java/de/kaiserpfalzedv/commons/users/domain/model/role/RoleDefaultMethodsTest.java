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

package de.kaiserpfalzedv.commons.users.domain.model.role;

import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-10
 */
@XSlf4j
class RoleDefaultMethodsTest {
  
  private Role sut;
  
  
  @BeforeEach
  void setUp() {
    sut = TestRole.builder().build();
  }
  
  @Test
  void shouldPrefixTheNameWithROLE_whenAskedForAuthority() {
    log.entry("shouldPrefixTheNameWithROLE_whenAskedForAuthority");
    
    String result = sut.getAuthority();
    log.debug("Querying authority. authority='{}', name='{}'", result, sut.getName());
    
    assertEquals("ROLE_" + sut.getName(), result);
    
    log.exit(result);
  }
}