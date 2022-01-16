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

package de.kaiserpfalzedv.commons.fileserver.jpa;

import de.kaiserpfalzedv.commons.core.files.File;
import de.kaiserpfalzedv.commons.core.files.FileData;
import de.kaiserpfalzedv.commons.core.files.HasMediaType;
import de.kaiserpfalzedv.commons.core.files.HasOutputStream;
import de.kaiserpfalzedv.commons.core.jpa.AbstractJPAEntity;
import de.kaiserpfalzedv.commons.core.jpa.Convertible;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.resources.Metadata;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * JPAFile -- A file saved inside the database (normally image files).
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
@RegisterForReflection
@Entity
@Table(
        name = "FILES",
        uniqueConstraints = {
                @UniqueConstraint(name = "FILES_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "FILES_NAME_UK", columnNames = {"NAMESPACE", "NAME"})
        }
)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class JPAFile extends AbstractJPAEntity implements HasId, HasName, HasMediaType, HasOutputStream, Serializable, Cloneable, Convertible<JPAFile, File> {
    public static final int MAX_FILE_LENGTH = 16_777_215;

    @Column(name = "NAMESPACE", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Length(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String nameSpace;

    @Column(name = "NAME", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Length(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String name;

    @Column(name = "OWNER", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @NonNull
    @Length(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String owner;

    @Column(name = "GRP", length = VALID_NAME_MAX_LENGTH, nullable = false)
    @Builder.Default
    @Length(min = VALID_NAME_MIN_LENGTH, max = VALID_NAME_MAX_LENGTH)
    private String group = "unspecified";

    @Column(name = "PERMISSIONS", length = 3, nullable = false)
    @Builder.Default
    @Length(min = 3, max = 3)
    private String permissions = "620";

    @Embedded
    @NonNull
    private JPAFileData file;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "PREVIEW_NAME")),
            @AttributeOverride(name = "data", column = @Column(name = "PREVIEW_DATA")),
            @AttributeOverride(name = "mediaType", column = @Column(name = "PREVIEW_MEDIATYPE"))
    })
    private JPAFileData preview;


    @Override
    @Transient
    public String getMediaType() {
        return getFile().getMediaType();
    }


    @Override
    public File to() {
        return File.builder()
                .withMetadata(
                        Metadata.of(File.KIND, File.API_VERSION, getNameSpace(), getName())
                                .withUid(getId())
                                .withAnnotations(Map.of(
                                        File.ANNOTATION_OWNER, getOwner(),
                                        File.ANNOTATION_GROUP, getGroup(),
                                        File.ANNOTATION_PERMISSIONS, getPermissions()
                                ))
                                .build()
                )
                .withSpec(buildSpec())
                .build();
    }

    private <C extends FileData, B extends FileData.FileDataBuilder<C, B>> FileData buildSpec() {
        @SuppressWarnings("unchecked")
        FileData.FileDataBuilder<C, B> result = (FileData.FileDataBuilder<C, B>) FileData.builder()
                .withFile(getFile().to());

        if (getPreview() != null) {
            result.withPreview(getPreview().to());
        }

        return result.build();
    }

    public static <C extends JPAFile, B extends JPAFileBuilder<C, B>> JPAFile from(File data) {
        @SuppressWarnings("unchecked")
        JPAFile.JPAFileBuilder<C, B> result = (JPAFileBuilder<C, B>) JPAFile.builder()
                .withId(data.getUid())
                .withCreated(data.getMetadata().getCreated())
                .withVersion(data.getGeneration())
                .withNameSpace(data.getNameSpace())
                .withFile(JPAFileData.from(data.getSpec().getFile()))
                .withPreview(JPAFileData.from(data.getSpec().getPreview()));

        data.getMetadata()
                .getAnnotation(File.ANNOTATION_OWNER)
                .ifPresent(result::withOwner);
        data.getMetadata()
                .getAnnotation(File.ANNOTATION_GROUP)
                .ifPresent(result::withGroup);
        data.getMetadata()
                .getAnnotation(File.ANNOTATION_PERMISSIONS)
                .ifPresent(result::withPermissions);

        return result.build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public JPAFile clone() throws CloneNotSupportedException {
        return toBuilder().build();
    }
}
