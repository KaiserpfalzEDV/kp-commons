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

package de.kaiserpfalzedv.commons.core.files;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FilePermissionCheckTEst --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-16
 */
@QuarkusTest
@Slf4j
public class FilePermissionCheckTest extends AbstractTestBase {
    private static final String NAME_SPACE = "namespace";
    private static final String NAME = "name";
    private static final String OWNER = "owner";
    private static final String GROUP = "group";
    private static final Map<String, String> LABELS = Map.of();
    private static final Map<String, String> ANNOTATIONS = Map.of(
            File.ANNOTATION_OWNER, OWNER,
            File.ANNOTATION_GROUP, GROUP,
            File.ANNOTATION_PERMISSIONS, "624"
    );

    private static final String FILE_NAME = "file_name.txt";
    private static final String FILE_MIMETYPE = MediaType.TEXT_PLAIN;
    private static final byte[] FILE_DATA = "Some content".getBytes(StandardCharsets.UTF_8);

    private static final File SUT = File.of(NAME_SPACE, NAME, ANNOTATIONS, LABELS)
            .spec(
                    FileData.builder()
                            .file(
                                    FileDescription.builder()
                                            .name(FILE_NAME)
                                            .mediaType(FILE_MIMETYPE)
                                            .data(FILE_DATA)
                                            .build()
                            )
                            .build()
            )
            .build();

    private static final String OTHER_USER = "other";
    private static final Set<String> VALID_GROUPS = Set.of("admin", "user", "group");
    private static final Set<String> INVALID_GROUPS = Set.of("admin", "user");


    @PostConstruct
    void init() {
        setTestSuite(getClass().getSimpleName());
        setLog(log);
    }


    @Test
    public void shouldGrantRequestWhenOwnerIsUser() {
        startTest("user-is-owner", OWNER, INVALID_GROUPS, File.READ_WRITE);
        assertTrue(
                SUT.hasAccess(OWNER, INVALID_GROUPS, File.READ_WRITE),
                "The access should always be granted for the owner."
        );
    }

    @Test
    public void shouldGrantRequestWhenUserHasTheRightGroupAndPermission() {
        startTest("user-has-group-permissions", OTHER_USER, VALID_GROUPS, File.READ);
        assertTrue(
                SUT.hasAccess(OTHER_USER, VALID_GROUPS, File.READ),
                "The access should be granted for this group and permission."
        );
    }

    @Test
    public void shouldGrantWriteAccessWhenUserHasNoMatchingGroup() {
        startTest("user-group-does-not-match", OTHER_USER, INVALID_GROUPS, File.WRITE);
        assertTrue(
                SUT.hasAccess(OTHER_USER, INVALID_GROUPS, File.WRITE),
                "Users not matching the group should have write access"
        );
    }

    @Test
    public void shouldDenyWriteAccessWhenUserHasMatchingGroup() {
        startTest("failed-group-must-not-write", OTHER_USER, VALID_GROUPS, File.WRITE);
        assertFalse(
                SUT.hasAccess(OTHER_USER, VALID_GROUPS, File.WRITE),
                "Users matching the group only should have read access"
        );
    }

    @Test
    public void shouldDenyRequestWhenNoUserIsGiven() {
        startTest("failing-no-user", null, VALID_GROUPS, File.READ);
        assertFalse(
                SUT.hasAccess(null, VALID_GROUPS, File.READ),
                "The access should have been denied since there is no user given."
        );
    }

    @Test
    public void shouldDenyRequestWhenNoPermissionIsGranted() {
        startTest("failing-missing-permission", OTHER_USER, VALID_GROUPS, File.WRITE);
        assertFalse(
                SUT.hasAccess(OTHER_USER, VALID_GROUPS, File.WRITE),
                "The access should have been denied since the requested permissions are not granted."
        );
    }

    @Test
    public void shouldDenyRequestWhenGroupIsInvalid() {
        startTest("failing-wrong-group", OTHER_USER, INVALID_GROUPS, File.READ);
        assertFalse(
                SUT.hasAccess(OTHER_USER, INVALID_GROUPS, File.READ),
                "The group should have no read permission."
        );
    }
}
