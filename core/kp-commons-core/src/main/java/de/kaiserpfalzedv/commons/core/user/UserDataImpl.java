/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.core.user;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.kaiserpfalzedv.commons.api.user.UserData;
import de.kaiserpfalzedv.commons.core.resources.DefaultResourceSpecImpl;
import de.kaiserpfalzedv.commons.core.resources.PointerImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * The basic data for every user.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(name = "userData", description = "Registered User.")
public class UserDataImpl extends DefaultResourceSpecImpl implements UserData {
    private static final long serialVersionUID = 0L;

    private static final String[] STRUCTURED_PROPERTIES = {
            ISSUER,
            SUBJECT
    };

    @Builder.Default
    private String name = null;

    @Builder.Default
    private String description = null;
    @Builder.Default
    private PointerImpl picture = null;

    @SuppressFBWarnings(value = "EI_EXPOSE_REF", justification = "It's the API design.")
    @JsonIgnore
    @Override
    public String[] getDefaultProperties() {
        return STRUCTURED_PROPERTIES;
    }

}
