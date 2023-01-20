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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * FrontendUser -- an excerpt for userdata for presentation in the web UI.
 *
 * This excerpt is only valid while the user is logged in. There is no user data in the DCIS but the
 * username from the OpenIDConnect IDP. If needed an administrator of both systems can link it to the
 * account data within the IDP to retrieve the email address.
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
