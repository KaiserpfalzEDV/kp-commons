/*
 * Copyright (c) 2021-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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
import java.util.ArrayList;

/**
 * ResourceList -- A serializable implementation of the List.
 *
 * @param <T> The elements of the list.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@SuppressWarnings("unused")
public class SerializableList<T extends Serializable> extends ArrayList<T> implements Serializable, Cloneable {
    private static final long serialVersionUID = 0L;

    @SuppressWarnings("unchecked")
    @Override
    public SerializableList<T> clone() {
        return (SerializableList<T>) super.clone();
    }
}
