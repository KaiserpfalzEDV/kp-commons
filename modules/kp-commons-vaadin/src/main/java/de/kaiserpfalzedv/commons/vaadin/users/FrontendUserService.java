/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.vaadin.users;

import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import de.kaiserpfalzedv.commons.core.libravatar.AvatarGenerator;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.MDC;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Locale;

/**
 * FrontendUserService -- The generator for the FrontendUser.
 *
 * This service get the {@link SecurityIdentity} and the {@link JsonWebToken} to
 * combine the information into the {@link FrontendUser} digested by all the Views on
 * this system.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@VaadinServiceEnabled
@RequestScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(callSuper = true, doNotUseGetters = true)
@Getter
@Slf4j
public class FrontendUserService {
    private final AvatarGenerator avatarGenerator;

    private final Locale DEFAULT_LANGUAGE = Locale.GERMAN;
    private SecurityIdentity identity;
    private JsonWebToken jwt;

    @Inject
    public void setSecurityIdentity(final SecurityIdentity identity) {
        log.debug("SecurityIdentity loaded into FrontendUserService. identity={}", identity);
        this.identity = identity;

        MDC.put("user", identity.getPrincipal().getName());

        log.trace("Credentials passed. credentials={}", identity.getCredentials());
    }

    @Inject
    public void setJsonWebToken(final JsonWebToken jwt) {
        log.debug("JWT loaded into FrontendUserService. jwt={}", jwt);
        this.jwt = jwt;
    }

    @Produces
    public FrontendUser frontendUser() {
        FrontendUser result;

        if (identity == null || jwt == null) {
            log.info("No identity or JWT set. identity={}, jwt={}", identity, jwt);
            result = FrontendUser.builder()
                    .name("Anonymous")
                    .email("support@paladins-inn.de")
                    .avatar(avatarGenerator.generateUri("support@paladins-inn.de"))
                    .locale(DEFAULT_LANGUAGE)

                    .build();
        } else {
            log.debug("Creating FrontendUser. identity={}, jwt={}", identity, jwt);
            String language = jwt.getClaim("locale");

            result = FrontendUser.builder()
                    .name(identity.getPrincipal().getName())
                    .email(jwt.getClaim("email"))
                    .avatar(avatarGenerator.generateUri(jwt.getClaim("email")))
                    .locale(language == null ? Locale.forLanguageTag(language) : DEFAULT_LANGUAGE)

                    .roles(identity.getRoles())

                    .build();
        }

        log.info("Created FrontendUser. result={}", result);
        return result;
    }
}
