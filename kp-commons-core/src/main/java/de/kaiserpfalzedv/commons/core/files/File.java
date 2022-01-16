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
import de.kaiserpfalzedv.commons.core.api.HasId;

import javax.persistence.Transient;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * File --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-16
 */
public interface File extends Serializable, Cloneable, HasId, Comparable<JPAFile> {

    String getNameSpace();

    String getOwner();

    @Transient
    @JsonIgnore
    String getName();

    @Transient
    @JsonIgnore
    String getMediaType();

    @Transient
    @JsonIgnore
    byte[] getData();

    @Transient
    @JsonIgnore
    OutputStream getFileStream();

    FileData getFile();
}
