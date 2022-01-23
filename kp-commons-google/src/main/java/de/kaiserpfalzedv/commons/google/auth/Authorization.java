/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Authorization --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Jacksonized
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class Authorization {
    @Builder.Default
    private final String keyFile = "/deployment/config/application-key.json";

    @Builder.Default
    private final List<String> scopes = List.of(DriveScopes.DRIVE_METADATA_READONLY, SheetsScopes.SPREADSHEETS);



    public Credential getGoogleCredentials() throws IOException, AuthorizationCreationFailedException {
        return getGoogleCredentials(keyFile);
    }

    public Credential getGoogleCredentials(@NotNull final String keyFile) throws AuthorizationCreationFailedException, IOException {
        File fp = new File(keyFile);
        if (!fp.exists()) {
            throw new IllegalStateException(String.format("File '%s' does not exist.", keyFile));
        }

        try (InputStream in = new FileInputStream(keyFile)) {
            try {
                return MockGoogleCredential.fromStream(in).createScoped(scopes);
            } catch (IOException e) {
                throw new AuthorizationCreationFailedException(keyFile, e);
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
