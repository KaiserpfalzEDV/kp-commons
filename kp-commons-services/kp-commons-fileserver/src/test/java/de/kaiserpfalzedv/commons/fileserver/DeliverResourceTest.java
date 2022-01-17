/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.fileserver;

import de.kaiserpfalzedv.commons.fileserver.services.DeliverResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static io.restassured.RestAssured.given;

/**
 * DeliverResourceTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.1.0  2022-01-16
 */
@QuarkusTest
@TestHTTPEndpoint(DeliverResource.class)
@Slf4j
public class DeliverResourceTest {
    @Test
    @TestSecurity
    public void shouldReturnTheSelectedFileWhenGivenCorrectNameSpaceAndName() {
        logTest("retrieve-by-namespace-and-name", "fileserver/liq-files-data");

        given()
                .when()
                .get("file/fileserver/liq-files-definition")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity
    public void shouldReturnTheFileWhenCorrectUidIsGiven() {
        logTest("retrieve-by-uid", "39062d79-e1a9-437a-b1b4-7dc783bd9eb4");

        given()
                .when()
                .get("file/39062d79-e1a9-437a-b1b4-7dc783bd9eb4")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity
    public void shouldNotFindTheFileWhenNameSpaceOrNameDoesNotMatch() {
        logTest("failed-retrieve-by-unknown-namespace-and-name");

        given()
                .when()
                .get("file/fileserver/invalid-name")
                .then()
                .statusCode(404);


        given()
                .when()
                .get("invalid-namespace/liq-files-data")
                .then()
                .statusCode(404);
    }


    @Test
    @TestSecurity
    public void shouldDenyTheFileWhenItIsNotPublic() {
        logTest("failed-non-public-retrieve", "39062d79-e1a9-437a-b1b4-7dc783bd9eb1");

        given()
                .when()
                .get("file/39062d79-e1a9-437a-b1b4-7dc783bd9eb1")
                .then()
                .statusCode(403);
    }

    private void logTest(final String test, final Object... parameters) {
        MDC.put("test", test);

        log.debug("Starting. test='{}', parameters={}", test, parameters);
    }

    @BeforeAll
    static void setUpLogging() {
        MDC.put("test-class", DeliverResourceTest.class.getSimpleName());

        log.info("Starting test... test-class='{}'", MDC.get("test-class"));
    }

    @AfterEach
    void removeMDC() {
        MDC.remove("test");
    }

    @AfterAll
    static void tearDownLogging() {
        MDC.remove("test");
        log.info("Ended test. test-class='{}'", MDC.get("test-class"));
        MDC.remove("test-class");
    }
}
