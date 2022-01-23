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

package de.kaiserpfalzedv.commons.core.rest;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HttpErrorGeneratorTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-17
 */
@QuarkusTest
@Slf4j
public class HttpErrorGeneratorTest extends AbstractTestBase {
    private static final Response.Status DEFAULT_STATUS = Response.Status.PAYMENT_REQUIRED;
    private static final Map<String, String> DEFAULT_DATA = Map.of(
            "key1", "value1",
            "key2", "value2",
            "key3", "value3"
    );
    public static final String DEFAULT_MESSAGE = "message";

    private final HttpErrorGenerator sut = new HttpErrorGenerator();

    public HttpErrorGeneratorTest() {
        super.setTestSuite(getClass().getSimpleName());
        super.setLog(log);
    }

    @Test
    public void shouldContainAllMappedData() {
        startTest(
                "check-http-problem",
                DEFAULT_STATUS, DEFAULT_MESSAGE, DEFAULT_DATA
        );

        HttpProblem result = sut.throwHttpProblem(DEFAULT_STATUS, DEFAULT_MESSAGE, DEFAULT_DATA);
        log.trace("Received {}. cause={}", result.getClass().getCanonicalName(), result);

        assertEquals(DEFAULT_STATUS, result.getStatus());
        assertEquals(DEFAULT_MESSAGE, result.getDetail());
        assertEquals(DEFAULT_DATA, result.getParameters());
    }
}
