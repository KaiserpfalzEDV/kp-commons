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

package de.kaiserpfalzedv.commons.google.drive;

import de.kaiserpfalzedv.commons.google.GoogleBaseException;
import lombok.Getter;
import lombok.ToString;

/**
 * GoogleDriveBaseException -- Problems while working with the google drive.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-04
 */
@Getter
@ToString
public abstract class GoogleDriveBaseException extends GoogleBaseException {
    private final String sheetId;

    public GoogleDriveBaseException(final String sheetId, final String message, final Throwable cause) {
        super(message, cause);

        this.sheetId = sheetId;
    }

    public GoogleDriveBaseException(final String sheetId, final Throwable cause) {
        this(String.format("Problem working with sheet '%s'.", sheetId), sheetId, cause);
    }



    public GoogleDriveBaseException(final String sheetId, final String message) {
        super(message);

        this.sheetId = sheetId;
    }

    public GoogleDriveBaseException(final String sheetId) {
        this(String.format("Problem working with sheet '%s'.", sheetId), sheetId);
    }
}
