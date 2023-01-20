/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.UUID;

/**
 * Resource --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-07
 */
public interface Resource<D extends Serializable> extends Pointer, HasMetadata {
    /**
     * @return Generated pointer for this resource.
     */
    @JsonIgnore
    Pointer toPointer();

    @JsonIgnore
    @Override
    default String getKind() {
        return getMetadata().getKind();
    }

    @JsonIgnore
    @Override
    default String getApiVersion() {
        return getMetadata().getApiVersion();
    }

    @JsonIgnore
    @Override
    default String getNameSpace() {
        return getMetadata().getNameSpace();
    }

    @JsonIgnore
    @Override
    default String getName() {
        return getMetadata().getName();
    }

    @JsonIgnore
    default UUID getUid() {
        return getMetadata().getUid();
    }

    @JsonIgnore
    default Integer getGeneration() {
        return getMetadata().getGeneration();
    }

    @JsonIgnore
    String getSelfLink();

    Resource<D> increaseGeneration();

    D getSpec();

    Status getStatus();
}
