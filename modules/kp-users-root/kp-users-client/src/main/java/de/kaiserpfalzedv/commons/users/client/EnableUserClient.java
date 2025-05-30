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
package de.kaiserpfalzedv.commons.users.client;

import de.kaiserpfalzedv.commons.users.client.reactive.ReactUserSecurityConfig;
import de.kaiserpfalzedv.commons.users.client.reactive.ReactUserDetailsService;
import de.kaiserpfalzedv.commons.users.client.service.*;
import de.kaiserpfalzedv.commons.users.messaging.EnableUsersMessaging;
import de.kaiserpfalzedv.commons.users.store.EnableUsersStore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * Enables the full user client functionality in a Spring application.
 * This includes user messaging and user store capabilities.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.1.0
 * @since 2025-05-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableUsersMessaging
@EnableUsersStore
@Import({
    ApiKeyAuthenticationManager.class,
    UserAuthenticationManager.class,
    KpUserDetailsService.class,
    UserAuthenticationService.class,
    UserLoggedInStateRepository.class,
    ReactUserSecurityConfig.class,
    ReactUserDetailsService.class,
})
public @interface EnableUserClient {}
