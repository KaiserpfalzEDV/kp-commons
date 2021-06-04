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

import de.kaiserpfalzedv.commons.google.GoogleBaseException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * GoogleAuthenticationException -- Some authentication problems.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-03
 */
@Getter
@ToString
public class GoogleCredentialsFileNotReadableException extends GoogleBaseException {
    private final String fileName;

    public GoogleCredentialsFileNotReadableException(final String fileName, final Throwable cause) {
        super(String.format("Problem working with file '%s'.", fileName), cause);

        this.fileName = fileName;
    }
}
