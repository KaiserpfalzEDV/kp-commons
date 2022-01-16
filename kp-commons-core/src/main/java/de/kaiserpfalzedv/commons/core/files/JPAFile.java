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

package de.kaiserpfalzedv.commons.core.files;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.jpa.AbstractEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.OutputStream;

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
                @UniqueConstraint(name = "FILES_ID_UK", columnNames = "ID")
        }
)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = JPAFile.JPAFileBuilder.class)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class JPAFile extends AbstractEntity implements File {
    @Column(name = "NAMESPACE", length = 50, nullable = false)
    private String nameSpace;

    @Column(name = "OWNER", length = 100, nullable = false)
    private String owner;

    @Embedded
    private FileData file;


    @Override
    @Transient
    @JsonIgnore
    public String getName() {
        return file.getName();
    }

    @Override
    @Transient
    @JsonIgnore
    public String getMediaType() {
        return file.getMediaType();
    }

    @Override
    @Transient
    @JsonIgnore
    public byte[] getData() {
        return file.getData();
    }

    @Override
    @Transient
    @JsonIgnore
    public OutputStream getFileStream() {
        return file.getDataStream();
    }

    @Override
    public int compareTo(@NotNull JPAFile o) {
        return new CompareToBuilder()
                .append(nameSpace, o.nameSpace)
                .append(owner, o.owner)
                .append(file, o.file)
                .toComparison();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public JPAFile clone() throws CloneNotSupportedException {
        return toBuilder().build();
    }
}
