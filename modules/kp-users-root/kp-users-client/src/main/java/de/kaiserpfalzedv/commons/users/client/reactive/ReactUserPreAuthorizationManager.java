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


import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@XSlf4j
public class ReactUserPreAuthorizationManager implements ReactiveAuthorizationManager<MethodInvocation> {
  @Override
  public Mono<AuthorizationDecision> check(final Mono<Authentication> authentication, final MethodInvocation object) {
    log.entry(authentication, object);
    
    AuthorizationDecision result = new AuthorizationDecision(true);
    
    return log.exit(Mono.just(result));
  }
}
