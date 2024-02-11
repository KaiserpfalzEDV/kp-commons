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

package de.kaiserpfalzedv.commons.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.kaiserpfalzedv.commons.api.store.OptimisticLockStoreException;
import de.kaiserpfalzedv.commons.api.user.UserStoreService;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.resources.Pointer;
import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * TestMemoryUserStore -- checks if the memory store behaves correctly.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Slf4j
public class TestMemoryUserStore extends AbstractTestBase {
    private static final UUID DATA_UID = UUID.randomUUID();
    private static final String DATA_NAMESPACE = "testNS";
    private static final String DATA_NAME = "testName";
    private static final OffsetDateTime DATA_CREATED = OffsetDateTime.now(Clock.systemUTC());

    private static final UUID OTHER_UID = UUID.randomUUID();
    private static final String OTHER_NAME = "otherName";
    private static final OffsetDateTime OTHER_CREATED = OffsetDateTime.now(Clock.systemUTC());

    private static final User DATA = User.builder()
            .metadata(
                    generateMetadata(DATA_CREATED, null)
            )
            .spec(
                    UserData.builder()
                            .name(DATA_NAME)
                            .properties(new HashMap<>())
                            .build()
            )
            .build();

    private static final User OTHER = User.builder()
                    .metadata(
                            generateMetadata(OTHER_CREATED, null)
                    )
                    .spec(
                            UserData.builder()
                                    .name(OTHER_NAME)
                                    .properties(new HashMap<>())
                                    .build()
                    )
                    .build();


    /**
     * service under test.
     */
    private final UserStoreService sut = new MemoryUserStore();


    public TestMemoryUserStore() {
        this.setTestSuite(this.getClass().getSimpleName());
        this.setLog(log);
    }

    @Test
    void shouldBeAMemoryUserStoreService() {
        this.startTest("store-is-memory-based");

        assertTrue(this.sut instanceof MemoryUserStore);
    }

    @Test
    void shouldSaveNewDataWhenDataIsNotStoredYet() {
        this.startTest("store-new-data");

        this.sut.save(DATA);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByNameSpaceAndName(DATA_NAMESPACE, DATA_NAME);
        log.trace("result: {}", result);

        assertTrue(result.isPresent(), "The data should have been stored!");
        assertEquals(DATA, result.get());
    }

    @Test
    void shouldSaveNewDataWhenDataIsAlreadyStoredYet() {
        this.startTest("update-stored-data");

        this.sut.save(DATA); // store data first time

        this.sut.save(DATA); // update data

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByNameSpaceAndName(DATA_NAMESPACE, DATA_NAME);
        log.trace("result: {}", result);

        assertTrue(result.isPresent(), "The data should have been stored!");
        Assertions.assertNotEquals(DATA.getGeneration(), result.get().getGeneration());

        Assertions.assertEquals(1, result.get().getGeneration());
    }

    /**
     * Sets up a metadata set.
     *
     * @return The generated metadata
     */
    private static Metadata generateMetadata(
            @NotNull final OffsetDateTime created,
            @SuppressWarnings("SameParameterValue") final OffsetDateTime deleted
    ) {
        return de.kaiserpfalzedv.commons.core.resources.Metadata.builder()
                .identity(Pointer.builder()
                        .kind(User.KIND)
                        .apiVersion(User.API_VERSION)
                        .nameSpace(DATA_NAMESPACE)
                        .name(DATA_NAME)
                        .build()
                )
                .uid(OTHER_UID)
                .created(created)
                .deleted(deleted)
                .build();
    }

    @Test
    public void shouldSaveOtherDataSetsWhenDataIsAlreadyStored() {
        this.startTest("save-other-data");

        this.sut.save(DATA);

        this.sut.save(OTHER);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(OTHER_UID);

        assertTrue(result.isPresent(), "there should be a user resource defined by this UID!");
        assertEquals(OTHER, result.get());
    }

    @Test
    public void shouldDeleteByNameWhenTheDataExists() {
        this.startTest("delete-existing-by-name");

        this.sut.save(DATA);
        this.sut.remove(DATA_NAMESPACE, DATA_NAME);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }

    @Test
    public void shouldDeleteByUidWhenTheDataExists() {
        this.startTest("delete-existing-by-uid");

        this.sut.save(DATA);
        this.sut.remove(DATA_UID);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }

    @Test
    public void shouldDeleteByObjectWhenTheDataExists() {
        this.startTest("delete-existing-by-uid");

        this.sut.save(DATA);
        this.sut.remove(DATA);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }
    @Test
    public void shouldDeleteByNameWhenTheDataDoesNotExists() {
        this.startTest("delete-non-existing-by-name");

        this.sut.remove(DATA_NAMESPACE, DATA_NAME);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }

    @Test
    public void shouldDeleteByUidWhenTheDataDoesNotExists() {
        this.startTest("delete-non-existing-by-uid");

        this.sut.remove(DATA_UID);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }

    @Test
    public void shouldDeleteByObjectWhenTheDataDoesNotExists() {
        this.startTest("delete-non-existing-by-uid");

        this.sut.remove(DATA);

        final Optional<de.kaiserpfalzedv.commons.api.user.User> result = this.sut.findByUid(DATA_UID);
        assertFalse(result.isPresent(), "Data should have been deleted!");
    }


    @Test
    void shouldThrowOptimisticLockExceptionWhenTheNewGenerationIsNotHighEnough() {
        this.startTest("throw-optimistic-lock-exception");

        this.sut.save(
                DATA.toBuilder()
                        .metadata(
                                ((Metadata)DATA.getMetadata()).toBuilder()
                                        .generation(100)
                                        .build()
                        )
                        .build()
        );

        try {
            this.sut.save(DATA);

            fail("There should have been an OptimisticLockStoreException!");
        } catch (final OptimisticLockStoreException e) {
            // every thing is fine. We wanted this exception
        }
    }
}
