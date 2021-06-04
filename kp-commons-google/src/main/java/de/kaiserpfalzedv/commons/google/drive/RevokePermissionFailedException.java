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

import lombok.extern.slf4j.Slf4j;

/**
 * RevokePermissionFailedException -- Revoking a permission failed.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-04
 */
@Slf4j
public class RevokePermissionFailedException extends GoogleDrivePermissionException {
    public static final String PERMISSION_MESSAGE_FORMAT = "Could not revoke role '%s' for '%s' from '%s'";

    public RevokePermissionFailedException(
            final String sheetId,
            final String email,
            final String role,
            final Throwable cause
    ) {
        super(sheetId, role, email, PERMISSION_MESSAGE_FORMAT, cause);
    }


    public RevokePermissionFailedException(
            final String sheetId,
            final String email,
            final String role
    ) {
        super(sheetId, role, email, PERMISSION_MESSAGE_FORMAT);
    }
}
