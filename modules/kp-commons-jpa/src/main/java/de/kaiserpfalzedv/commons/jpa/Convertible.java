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

package de.kaiserpfalzedv.commons.jpa;

/**
 * Convertible -- This object can be converted.
 * <p>
 * The conversion to the B representation is specified by this interface.
 * The conversion from B to A should be a static method from(B data) returning
 * the new object.
 *
 * @param <A> First representation.
 * @param <B> Second representation.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.2  2022-01-16
 * @since 2.0.0  2022-01-16
 */
public interface Convertible<A, B> {
    /**
     * Converts the resource to the data representation.
     *
     * @return the converted data resource.
     */
    B to();
}
