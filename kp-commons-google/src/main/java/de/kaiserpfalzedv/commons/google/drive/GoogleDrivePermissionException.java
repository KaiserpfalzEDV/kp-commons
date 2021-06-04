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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * GoogleDrivePermissionException -- The granting or revoking of a permission failed.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-04
 */
@Getter
public abstract class GoogleDrivePermissionException extends GoogleDriveBaseException {
    final String sheetId;
    final String email;
    final String role;

    public GoogleDrivePermissionException(
            final String sheetId,
            final String role,
            final String email,
            final String messageFormat,
            final Throwable cause
    ) {
        super(sheetId, String.format(messageFormat, role, sheetId, email), cause);

        this.sheetId = sheetId;
        this.email = email;
        this.role = role;
    }

    public GoogleDrivePermissionException(
            final String sheetId,
            final String role,
            final String email,
            final String messageFormat
    ) {
        super(sheetId, String.format(messageFormat, role, sheetId, email));

        this.sheetId = sheetId;
        this.email = email;
        this.role = role;
    }
}
