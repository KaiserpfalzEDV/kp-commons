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

package de.kaiserpfalzedv.commons.api;

/**
 * A wrapper around a checked exception.
 *
 * @author rlichti
 * @version 2.0.0 2021-09-08
 * @since 2.0.0 2021-09-08
 */
public class BaseWrappedException extends BaseSystemException {
    public BaseWrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseWrappedException(Throwable cause) {
        super(cause);
    }

    public BaseWrappedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
