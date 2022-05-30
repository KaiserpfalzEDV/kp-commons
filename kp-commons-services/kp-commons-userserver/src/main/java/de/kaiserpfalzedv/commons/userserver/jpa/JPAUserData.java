/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.userserver.jpa;

import de.kaiserpfalzedv.commons.core.jpa.Convertible;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.user.UserData;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * FileData -- An embedded file (byte coded).
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.3.0  2022-05-30
 * @since 2.3.0  2022-05-30
 */
@Embeddable
@RegisterForReflection
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class JPAUserData implements HasName, Serializable, Cloneable, Convertible<JPAUserData, UserData> {
    @Column(name = "NAME", length = VALID_NAME_MAX_LENGTH)
    @NonNull
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    @ToString.Include
    private String name;

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public JPAUserData clone() throws CloneNotSupportedException {
        return toBuilder().build();
    }

    public static JPAUserData from(final UserData data) {
        if (data == null) {
            return null;
        }
        return JPAUserData.builder()
                .name(data.getName())
                .build();
    }

    @Override
    public UserData to() {
        return UserData.builder()
                .name(getName())
                .build();
    }
}
