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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.DriveScopes;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * AuthorizationProducer -- This is the producer for Google Credentials.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-03
 */
@ApplicationScoped
@Slf4j
@Setter
@ToString
public class GoogleCredentialProvider {
    @ConfigProperty(name = "google.credentials.file")
    Optional<String> credentialsFile;

    @ConfigProperty(name = "google.credentials.scopes")
    List<String> scopes;

    private Authorization authorization;

    /**
     * Creates the google credentials needed to talk to the Google API.
     * @return The credentials to be used when connecting to the Google API.
     * @throws GoogleCredentialsFileNotReadableException The specified file could not be read.
     * @throws NoGoogleCredentialsFileException There is no file name specified to be used.
     */
    @Produces
    public Credential createCredential() throws GoogleCredentialsFileNotReadableException, NoGoogleCredentialsFileException {
        if (authorization == null) {
            createAuthorization();
        }

        try {
            return authorization.getGoogleCredentials();
        } catch (IOException | IllegalStateException | AuthorizationCreationFailedException e) {
            throw new GoogleCredentialsFileNotReadableException(credentialsFile.orElseThrow(() -> new NoGoogleCredentialsFileException("")), e);
        }
    }

    private synchronized void createAuthorization() throws NoGoogleCredentialsFileException {
        if (authorization != null) {
            log.debug("authorization already created: {}", authorization);
            return;
        }

        log.info("Creating authorization. credentialsFile='{}', scopes={}", credentialsFile.orElse("./."), scopes);

        Authorization.AuthorizationBuilder authorizationBuilder = Authorization.builder()
                .keyFile(credentialsFile.orElseThrow(() -> new NoGoogleCredentialsFileException("")));

        if (!scopes.isEmpty()) {
            authorizationBuilder.scopes(scopes);
        }

        authorization = authorizationBuilder.build();
    }
}
