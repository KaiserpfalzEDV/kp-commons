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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.resources.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * FileResource -- An image or any other file saved for the system.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0  2021-12-31
 * @since 2.0.0  2022-01-16
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Slf4j
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "A file saved in the system.")
public class File extends Resource<FileData> {
    public static final String KIND = "File";
    public static final String API_VERSION = "v1";

    public static final String ANNOTATION_OWNER = "de.kaiserpfalz-edv.files/owner";
    public static final String ANNOTATION_GROUP = "de.kaiserpfalz-edv.files/group";
    public static final String ANNOTATION_PERMISSIONS = "de.kaiserpfalz-edv.files/permissions";

    private static final int[] FULL_PERMISSION = {6, 6, 6};
    private static final int[] NO_PERMISSION = {0, 0, 0};
    public static final int READ = 2;
    public static final int WRITE = 4;
    public static final int READ_WRITE = 6;

    @BsonIgnore
    @JsonIgnore
    public Optional<String> getOwner() {
        return getMetadata().getAnnotation(ANNOTATION_OWNER);
    }

    @BsonIgnore
    @JsonIgnore
    public Optional<String> getGroup() {
        return getMetadata().getAnnotation(ANNOTATION_GROUP);
    }

    @BsonIgnore
    @JsonIgnore
    public int[] getPermissions() {
        if (getMetadata().getAnnotation(ANNOTATION_PERMISSIONS).isEmpty()) {
            return FULL_PERMISSION;
        }

        String permissions = getMetadata().getAnnotation(ANNOTATION_PERMISSIONS).get();
        if (permissions.length() != 3) {
            return NO_PERMISSION;
        }

        return new int[]{
                Integer.parseInt(permissions.substring(0, 1), 10),
                Integer.parseInt(permissions.substring(1, 2), 10),
                Integer.parseInt(permissions.substring(2, 3), 10)
        };
    }

    /**
     * Checks if the user has access to this file.
     * <p>
     * The owning user is always granted access to the file. And if no owner is set, the file is always accessable.
     *
     * @param user       The requesting user.
     * @param groups     The groups this user belongs to.
     * @param permission The requested permission ({@link #READ}, {@link #WRITE} or {@link #READ_WRITE}).
     * @return TRUE if the access is granted, FALSE if the access is denied.
     */
    @BsonIgnore
    @JsonIgnore
    public boolean hasAccess(@NotNull final String user, @NotNull final Set<String> groups, final int permission) {
        log.debug(
                "Access check. owner='{}', group='{}', permissions={}, user='{}', groups={}, requested={}",
                getOwner(), getGroup(), getPermissions(), user, groups, permission
        );

        boolean result;
        int[] permissions = getPermissions();

        if (user == null) {
            log.debug("Access denied. owner='{}', user='{}'", getOwner(), user);
            return false;
        }

        if (getOwner().isEmpty() || getOwner().get().equalsIgnoreCase(user)) {
            log.debug("Access granted. owner='{}', user='{}'", getOwner(), user);
            return true;
        }

        result = permissions[2] == permission || permissions[1] == READ_WRITE;
        log.trace("Access check after others: granted={}", result);

        if (getGroup().isPresent()) {
            String group = getGroup().get();
            log.trace("Access check for group permissions is enabled. group='{}'", group);

            if (groups.contains(group)) {
                result = permissions[1] == permission || permissions[1] == READ_WRITE;
            }
        }
        log.trace("Access check after group: granted={}", result);

        log.debug(
                "Access check. granted={}, owner='{}', group='{}', permissions={}, user='{}', groups={}, requested={}",
                result, getOwner(), getGroup(), getPermissions(), user, groups, permission
        );
        return result;
    }


    public static <C extends File, B extends File.FileBuilder<C, B>> FileBuilder<C, B> of(final String nameSpace, final String name) {
        //noinspection unchecked
        return (FileBuilder<C, B>) File.builder()
                .metadata(
                        Metadata.of(KIND, API_VERSION, nameSpace, name)
                                .build()
                );
    }

    public static <C extends File, B extends File.FileBuilder<C, B>> FileBuilder<C, B> of(
            final String nameSpace,
            final String name,
            final Map<String, String> annotations,
            final Map<String, String> labels
    ) {
        //noinspection unchecked
        return (FileBuilder<C, B>) File.builder()
                .metadata(
                        Metadata.of(KIND, API_VERSION, nameSpace, name)
                                .annotations(annotations)
                                .labels(labels)
                                .build()
                );
    }
}
