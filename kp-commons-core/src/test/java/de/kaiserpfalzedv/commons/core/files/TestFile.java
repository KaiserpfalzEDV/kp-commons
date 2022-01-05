/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.kaiserpfalzedv.commons.core.files;

import de.kaiserpfalzedv.commons.core.resources.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * TestMemoryFileStore -- checks if the memory store behaves correctly.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0  2021-05-24
 */
@Slf4j
public class TestFile {
    private static final UUID DATA_UID = UUID.randomUUID();
    private static final String DATA_NAMESPACE = "testNS";
    private static final String DATA_NAME = "testName";
    private static final OffsetDateTime DATA_CREATED = OffsetDateTime.now(Clock.systemUTC());
    private static final String DATA_API_KEY = "test-api-key";
    private static final String BASE64_DATA = "RGFzIGhpZXIgaXN0IGVpbmZhY2ggbnVyIGVpbiBCZWlzcGllbGZpbGUK";

    private static final File DATA = File.builder()
            .withKind(File.KIND)
            .withApiVersion(File.API_VERSION)
            .withNameSpace(DATA_NAMESPACE)
            .withName(DATA_NAME)
            .withUid(DATA_UID)

            .withMetadata(
                    generateMetadata(DATA_CREATED)
            )
            .withSpec(
                    FileData.builder()
                            .withDescription(DATA_API_KEY)
                            .withData(BASE64_DATA)
                            .build()
            )
            .build();

    @BeforeAll
    static void setUp() {
        MDC.put("test-class", TestFile.class.getSimpleName());
    }

    @AfterAll
    static void tearDown() {
        MDC.clear();
    }

    /**
     * Sets up a metadata set.
     *
     * @return The generated metadata
     */
    @SuppressWarnings("SameParameterValue")
    private static Metadata generateMetadata(
            final OffsetDateTime created
    ) {
        HashMap<String, String> annotations = new HashMap<>(1);
        annotations.put("valid", "test");

        HashMap<String, String> labels = new HashMap<>();
        labels.put("test", "valid");

        return Metadata.builder()
                .withCreated(created)

                .withAnnotations(annotations)
                .withLabels(labels)

                .build();
    }

    @Test
    void shouldFindAnnotationWhenItIsSet() {
        MDC.put("test", "read-valid-annotation");

        Assertions.assertTrue(DATA.getMetadata().isAnnotated("valid"));
        Assertions.assertEquals("test", DATA.getMetadata().getAnnotations().get("valid"));
    }

    @Test
    void shouldNotFindAnnotationWhenItIsAbsent() {
        MDC.put("test", "read-invalid-annotation");

        Assertions.assertFalse(DATA.getMetadata().isAnnotated("invalid"));
    }

    @Test
    void shouldFindLabelWhenItIsSet() {
        MDC.put("test", "read-valid-label");

        Assertions.assertTrue(DATA.getMetadata().isLabeled("test"));
        Assertions.assertEquals("valid", DATA.getMetadata().getLabels().get("test"));
    }

    @Test
    void shouldNotFindLabelWhenItIsAbsent() {
        MDC.put("test", "read-invalid-label");

        Assertions.assertFalse(DATA.getMetadata().isAnnotated("not-there"));
    }

    @AfterEach
    void tearDownEach() {
        MDC.remove("test");
    }
}
