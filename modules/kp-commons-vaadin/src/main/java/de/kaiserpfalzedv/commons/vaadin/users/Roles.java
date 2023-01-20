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

/**
 * Roles - the roles used in the permission system of this application.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
public enum Roles {
    /** A normal player of the system. */
    player,
    /** a registered GM who may schedule missions or add special missions from his game table. */
    gm,
    /** member of the orga team of torganized play. */
    orga,
    /** A judge within torganized play.*/
    judge,
    /** A system operator of the technical system for management of torganized play. */
    admin;
}
