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

package de.kaiserpfalzedv.commons.fileserver;

import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFile;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileData;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileRepository;
import de.kaiserpfalzedv.commons.fileserver.services.FileResource;
import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

/**
 * FileServiceTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-01
 */
@QuarkusTest
@TestHTTPEndpoint(FileResource.class)
@Slf4j
public class FileResourceTest extends AbstractTestBase {
    @Inject
    JPAFileRepository repository;

    @PostConstruct
    void init() {
        setTestSuite(getClass().getSimpleName());
        setLog(log);

        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    public void shouldReturnFullListWhenCalledWithoutParameters() {
        startTest("list-files");

        given()
                .when()
                .get()
                .then()
                .statusCode(200);
    }


    @Test
    @TestSecurity(user = "user", roles = "user")
    public void shouldReturnTheSelectedFileWhenGivenCorrectNameSpaceAndName() {
        startTest("retrieve-by-namespace-and-name");

        given()
                .when()
                .get("fileserver/liq-files-data")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    public void shouldNotFindTheFileWhenNameisInvalid() {
        startTest("failed-retrieve-by-unknown-name");

        given()
                .when()
                .get("fileserver/invalid-name")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    public void shouldNotFindTheFileWhenNameSpaceisInvalid() {
        startTest("failed-retrieve-by-unknown-namespace");

        given()
                .when()
                .get("invalid-namespace/liq-files-data")
                .then()
                .statusCode(404);
    }

    @Transactional
    @Test
    @TestSecurity(user = "user", roles = "user")
    public void shouldUpdateTheFileWhenUserMatch() {
        startTest("update-owned-file");

        JPAFile orig = JPAFile.builder()
                .withNameSpace("update-owned-file")
                .withName("update-owned-file")
                .withOwner("user")
                .withGroup("user")
                .withPermissions("000")
                .withFile(
                        JPAFileData.builder()
                                .withName("update-owned-file.txt")
                                .withMediaType(MediaType.TEXT_PLAIN)
                                .withData("content".getBytes(StandardCharsets.UTF_8))
                                .build()
                )
                .build();
        repository.persistAndFlush(orig);
        log.trace("Created file to update. data={}", orig);

        JPAFile update = orig.toBuilder()
                .withPreview(
                        JPAFileData.builder()
                                .withName("update-owned-preview.txt")
                                .withMediaType(MediaType.TEXT_PLAIN)
                                .withData("con...".getBytes(StandardCharsets.UTF_8))
                                .build()
                )
                .build();

        File result = given()
                .when()
                .contentType(ContentType.JSON)
                .body(update)
                .put()
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON).extract().response()
                .prettyPeek()
                .jsonPath().get();

        assertThat(result.getSpec().getPreview().getName(), equals(update.getPreview().getName()));
        assertThat(result.getMetadata().getModified(), not(equals(orig.getCreated())));

        // cleanup
        repository.deleteById(result.getUid());
    }

    @Test
    @TestSecurity
    public void shouldReturnNotAllowedWhenNotAuthenticated() {
        startTest("fail-unauthenticated");

        given()
                .when()
                .get()
                .then()
                .statusCode(401);
    }
}
