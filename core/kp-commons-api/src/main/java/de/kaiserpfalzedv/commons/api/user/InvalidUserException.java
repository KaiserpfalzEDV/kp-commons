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

package de.kaiserpfalzedv.commons.api.user;

import de.kaiserpfalzedv.commons.api.BaseException;

/**
 * InvalidUserException -- The user has no data set.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public class InvalidUserException extends BaseException {
    /**
     * The user data is invalid. No spec is defined.
     *
     * @param user The invalid user
     */
    public InvalidUserException(final User user) {
        super(String.format("The user '%s/%s' has no data set!", user.getNameSpace(), user.getName()));
    }
}
