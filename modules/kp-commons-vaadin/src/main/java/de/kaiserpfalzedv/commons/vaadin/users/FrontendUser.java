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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * <p>FrontendUser -- an excerpt for userdata for presentation in the web UI.</p>
 *
 * <p>This excerpt is only valid while the user is logged in. There is no user data in the DCIS but the
 * username from the OpenIDConnect IDP. If needed an administrator of both systems can link it to the
 * account data within the IDP to retrieve the email address.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@SuperBuilder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
@Jacksonized
public class FrontendUser {
    /** The name of the user. */
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String name;

    /** The email address of the user. */
    private final String email;

    /** The avatar of the user (calculated with the email address) */
    private final String avatar;

    /** The locale of this user. */
    @ToString.Include
    private final Locale locale;

    /** This set contains all roles of this user. */
    @Builder.Default
    private final Set<String> roles = new HashSet<>();


    /**
     * Checks if the user is in the specified role.
     *
     * @param role the role to check for.
     */
    @JsonIgnore
    public boolean isInRole(final String role) {
        return roles.contains(role);
    }

    /**
     * Checks if the user is in the specified role.
     *
     * @param role the role to check for.
     */
    @JsonIgnore
    public boolean isInRole(final Roles role) {
        return roles.contains(role.name());
    }
}
