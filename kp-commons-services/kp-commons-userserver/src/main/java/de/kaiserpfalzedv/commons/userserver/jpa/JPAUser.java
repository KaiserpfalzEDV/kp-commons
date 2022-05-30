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

import de.kaiserpfalzedv.commons.core.jpa.AbstractJPAEntity;
import de.kaiserpfalzedv.commons.core.jpa.Convertible;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.user.User;
import de.kaiserpfalzedv.commons.core.user.UserData;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

/**
 * JPAUser -- A user saved inside the database (normally users).
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.3.0  2022-05-30
 * @since 2.3.0  2022-05-30
 */
@RegisterForReflection
@Entity
@Table(
        schema = "USERSTORE",
        name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(name = "USERS_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "USERS_NAME_UK", columnNames = {"NAMESPACE", "NAME"})
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class JPAUser extends AbstractJPAEntity implements HasId, HasName, Serializable, Cloneable, Convertible<JPAUser, User> {
    @Column(name = "NAMESPACE", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String nameSpace;

    @Column(name = "ISSUER", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String issuer;

    @Column(name = "SUBJECT", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Size(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String subject;


    @Embedded
    @NonNull
    private JPAUserData user;

    public String getName() {
        return user.getName();
    }

    @Override
    public User to() {
        return User.builder()
                .metadata(
                        Metadata.of(User.KIND, User.API_VERSION, getNameSpace(), getName())
                                .uid(getId())
                                .labels(Map.of(
                                        UserData.ISSUER, getIssuer(),
                                        UserData.SUBJECT, getSubject()
                                ))
                                .build()
                )
                .spec(buildSpec())
                .build();
    }

    private <C extends UserData, B extends UserData.UserDataBuilder<C, B>> UserData buildSpec() {
        @SuppressWarnings("unchecked")
        UserData.UserDataBuilder<C, B> result = (UserData.UserDataBuilder<C, B>) UserData.builder()
                .name(user.getName())
                //FIXME 2022-05-30 rlichti picture pointer needs to be saved and restored
                ;

        return result.build();
    }

    public static <C extends JPAUser, B extends JPAUserBuilder<C, B>> JPAUser from(User data) {
        @SuppressWarnings("unchecked")
        JPAUser.JPAUserBuilder<C, B> result = (JPAUserBuilder<C, B>) JPAUser.builder()
                .id(data.getUid())
                .created(data.getMetadata().getCreated())
                .version(data.getGeneration())
                .nameSpace(data.getNameSpace())
                .user(JPAUserData.from(data.getSpec()));

        data.getMetadata()
                .getLabel(UserData.ISSUER)
                .ifPresent(result::issuer);
        data.getMetadata()
                .getLabel(UserData.SUBJECT)
                .ifPresent(result::subject);

        return result.build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public JPAUser clone() throws CloneNotSupportedException {
        return toBuilder().build();
    }
}
