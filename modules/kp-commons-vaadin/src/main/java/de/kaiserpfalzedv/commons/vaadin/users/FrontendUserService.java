/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
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
 * <p>FrontendUserService -- The generator for the FrontendUser.</p>
 *
 * <p>This service get the {@link SecurityIdentity} and the {@link JsonWebToken} to
 * combine the information into the {@link FrontendUser} digested by all the Views on
 * this system.</p>
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
                    .locale(language != null ? Locale.forLanguageTag(language) : DEFAULT_LANGUAGE)

                    .roles(identity.getRoles())

                    .build();
        }

        log.info("Created FrontendUser. result={}", result);
        return result;
    }
}
