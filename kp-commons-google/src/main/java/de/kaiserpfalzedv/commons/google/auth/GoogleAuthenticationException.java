/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.google.auth;

import de.kaiserpfalzedv.commons.google.GoogleBaseSystemException;
import lombok.ToString;

/**
 * GoogleAuthenticationException -- Some authentication problems.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-03
 */
@ToString
public abstract class GoogleAuthenticationException extends GoogleBaseSystemException {
    public GoogleAuthenticationException(final String message) {
        super(message);
    }

    public GoogleAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GoogleAuthenticationException(final Throwable cause) {
        super(cause);
    }

    public GoogleAuthenticationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
