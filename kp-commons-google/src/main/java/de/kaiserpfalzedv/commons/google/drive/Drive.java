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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Drive -- Lists files in a google drive.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-03
 */
@SuppressWarnings("unused")
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Getter
@ToString
@Slf4j
public class Drive {
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);

    private final NetHttpTransport transport;
    private final JsonFactory factory;
    private Credential credential;
    private String name;

    private String mimeType;
    private Integer pageSize;
    private String pageToken;

    public com.google.api.services.drive.Drive open() {
        return new com.google.api.services.drive.Drive.Builder(transport, factory, credential)
                .setApplicationName(name)
                .build();
    }

    public FileList list() throws IOException {
        com.google.api.services.drive.Drive.Files.List result = open().files().list();

        setMimeType(result);
        setPageSize(result);
        setPageToken(result);

        return result.execute();
    }

    private void setMimeType(final com.google.api.services.drive.Drive.Files.List result) {
        if (mimeType != null) {
            log.trace("Limiting list to defined file types. mimeType='{}", mimeType);
               result.setQ(String.format("mimeType == '%s'", mimeType));
        } else {
            log.trace("Looking for all file types. Not limited by MimeType.");
        }
    }

    private void setPageSize(final com.google.api.services.drive.Drive.Files.List result) {
        if (pageSize != null) {
            log.trace("Listing only {} entries per page.", pageSize);
            result.setPageSize(pageSize);
        } else {
            log.trace("Listing all entries.");
        }
    }

    private void setPageToken(final com.google.api.services.drive.Drive.Files.List result) {
        if (pageToken != null) {
            log.trace("Listing page '{}'.", pageToken);
            result.setPageToken(pageToken);
        } else {
            log.trace("Listing first page.");
        }
    }

    /**
     * Grants the given role on the given sheet to the given email.
     *
     * @param sheetId Sheet to grant the permission to.
     * @param role Role to be granted on the sheet.
     * @param email User this role is granted on the sheet.
     * @throws GrantPermissionFailedException If there is a problem in granting the permission.
     */
    public void grantPermission(final String sheetId, final String role, final String email) throws GrantPermissionFailedException {
        log.debug("Trying to grant permission to user ... sheetId='{}', email='{}', role='{}'",
                sheetId, email, role);


        Permission p = new Permission()
                .setType("user")
                .setRole(role)
                .setEmailAddress(email);


        try {
            open().permissions()
                    .create(sheetId, p)
                    .setTransferOwnership("owner".equals(role))
                    .execute();

            log.info("Granted permission to user. sheetId='{}', email='{}', role='{}'",
                    sheetId, email, role);
        } catch (IOException e) {
            throw new GrantPermissionFailedException(sheetId, email, role, e);
        }
    }


    /**
     * Revokes the given role on the given sheet from the given email. If the user of the email address does not have
     * the permission, no error is returned. It's called idempotency ...
     *
     * @param sheetId Sheet to grant the permission to.
     * @param role Role to be granted on the sheet.
     * @param email User this role is granted on the sheet.
     * @throws RevokePermissionFailedException If there is a problem while revoking the permission.
     */
    public void revokePermission(final String sheetId, final String role, final String email)
            throws RevokePermissionFailedException {
        log.debug("Trying to revoke permission from user ... sheetId='{}', email='{}', role='{}'",
                sheetId, email, role);

        com.google.api.services.drive.Drive service = open();

        try {
            for (Permission e : service.permissions().list(sheetId).execute().getPermissions()) {
                if (e.getEmailAddress().equals(email) && e.getRole().equals(role) && "user".equals(e.getType())) {
                    service.permissions().delete(sheetId, e.getId()).execute();

                    log.info("Revoked permission from user. sheetId='{}', email='{}', role='{}'",
                            sheetId, email, role);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RevokePermissionFailedException(sheetId, email, role, e);
        }
    }
}
