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

package de.kaiserpfalzedv.commons.users.client.reactive;


import de.kaiserpfalzedv.commons.users.client.service.KpUserDetailsService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@Service
@Import({
    KpUserDetailsService.class,
})
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class ReactUserDetailsService implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {
  private final KpUserDetailsService details;
  
  @Override
  public Mono<UserDetails> updatePassword(final UserDetails user, final String newPassword) {
    log.entry(user, newPassword);
    
    log.warn("Password update is not supported in the ReactUserDetailsService. Returning null.");
    
    return log.exit(Mono.just(user));
  }
  
  @Override
  public Mono<UserDetails> findByUsername(final String username) {
    log.entry(username);

    UserDetails result = details.loadUserByUsername(username);
    
    return log.exit(Mono.justOrEmpty(result));
  }
}
