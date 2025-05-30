/*
 * Copyright (c) 2024-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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
package de.kaiserpfalzedv.commons.users.store;

import de.kaiserpfalzedv.commons.spring.events.SpringEventBus;
import de.kaiserpfalzedv.commons.users.store.model.apikey.ApiKeyToJPAImpl;
import de.kaiserpfalzedv.commons.users.store.model.role.RoleToJpaImpl;
import de.kaiserpfalzedv.commons.users.store.model.user.UserToJpaImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;


/**
 * Enables the user store in a Spring Boot application.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-09-27
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableJpaRepositories(basePackageClasses = EnableUsersStore.class)
@EntityScan(basePackageClasses = EnableUsersStore.class)
@ComponentScan(basePackageClasses = EnableUsersStore.class)
@Import({
    ApiKeyToJPAImpl.class,
    RoleToJpaImpl.class,
    UserToJpaImpl.class,
    SpringEventBus.class,
})
public @interface EnableUsersStore {}
