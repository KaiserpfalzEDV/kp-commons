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

package de.kaiserpfalzedv.commons.api.data;

import java.io.Serializable;

import de.kaiserpfalzedv.commons.api.resources.HasRevision;

/**
 * Persisted -- The base interface for all resources that are persisted.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.0.0  2024-09-22
 * @since 2.0.0  2023-01-06
 * 
 * @param T The type of the ID (I normally use UUID).
 */
public interface Persisted<T extends Serializable> extends HasRevision<T>, Cloneable {
}
