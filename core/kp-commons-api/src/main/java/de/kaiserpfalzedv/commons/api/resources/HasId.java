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

package de.kaiserpfalzedv.commons.api.resources;

import java.io.Serializable;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * HasId -- Has an Id of type T.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.0.0  2024-09-22
 * @since 0.1.0  2021-04-18
 * 
 * @Param T The type of ID (I usually use UUID).
 */
public interface HasId<T extends Serializable> {
    @Schema(
            name = "id",
            description = "The id of a resource."
    )
    @NotNull
    T getId();
}
