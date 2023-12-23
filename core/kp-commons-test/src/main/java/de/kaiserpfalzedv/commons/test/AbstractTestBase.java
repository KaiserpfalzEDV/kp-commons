/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.test;

import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.validation.constraints.NotNull;

/**
 * AbstractTestBase -- A base class for common junit tests.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-18
 */
@Setter
public class AbstractTestBase {
    private static final String MDC_TEST_SUITE_KEY = "test-class";
    private static final String MDC_TEST_KEY = "test";

    private Logger log = LoggerFactory.getLogger("test");
    private String testSuite = "unspecified";

    public void setTestSuite(final String suite) {
        // replace due to Quarkus subclassing ...
        testSuite = suite.replace("_Subclass", "");
    }

    protected void startTest(@NotNull final String test, Object... params) {
        MDC.put(MDC_TEST_SUITE_KEY, testSuite);
        MDC.put(MDC_TEST_KEY, test);

        log.info("Starting test ... test='{}', params={}", test, params);
    }

    @AfterEach
    void removeTestFromMDC() {
        log.debug("Test finished. test='{}'", MDC.get(MDC_TEST_KEY));

        MDC.remove(MDC_TEST_KEY);
        MDC.remove(MDC_TEST_SUITE_KEY);
    }
}
