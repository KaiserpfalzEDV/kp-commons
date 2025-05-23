/*
 * Copyright (c) 2024-2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package de.kaiserpfalzedv.commons.users.messaging;

import de.kaiserpfalzedv.commons.spring.events.SpringEventBus;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.1.0
 * @since 2024-09-27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({
    ReceiveUserActivityConfig.class,
    ReceiveUserArbitrationEventsConfig.class,
    ReceiveUserModificationEventsConfig.class,
    ReceiveUserStateEventsConfig.class,
    SpringEventBus.class
})
public @interface EnableUsersMessaging {}
