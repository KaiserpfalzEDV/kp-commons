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

package de.kaiserpfalzedv.commons.api.store;

import de.kaiserpfalzedv.commons.api.resources.Resource;

import java.util.Optional;
import java.util.UUID;

/**
 * StoreService -- A generic store service definition for persistent TOMB resources.
 *
 * @param <T> The resource type to be stored.
 */
public interface StoreService<T extends Resource<?>> {
    /**
     * @param nameSpace the namespace of the object to load.
     * @param name      the name of the object to load.
     * @return the object or an empty {@link Optional}.
     */
    Optional<T> findByNameSpaceAndName(final String nameSpace, final String name);

    /**
     * @param uid The uid of the data set to load.
     * @return the object or an empty {@link Optional}.
     */
    Optional<T> findByUid(final UUID uid);

    /**
     * Persists the given Resource. If the Resource already is stored and the generations are equal, then the
     * generation is incremented by 1. If the generation of the object is less than the generation of the data already
     * in the store, then the {@link OptimisticLockStoreException} is thrown.
     * <p>
     * On the other hand: if the namespace and name matches but uid not, then the object is considered a duplicate and
     * the persisting of the new object is denied.
     *
     * @param object The data to store.
     * @return The stored data (the generation may be incremented by 1).
     * @throws OptimisticLockStoreException Thrown if the generation inside the store is higher than the generation of
     *                                      the object given in the request.
     * @throws DuplicateStoreException      If name and nameSpace matches but uid not, then an object is considered a
     *                                      duplicate.
     */
    @SuppressWarnings("UnusedReturnValue")
    T save(final T object) throws OptimisticLockStoreException, DuplicateStoreException;

    /**
     * Remove the object.
     *
     * @param object the object to be removed.
     */
    void remove(final T object);

    /**
     * Remove the object specified by nameSpace and name.
     *
     * @param nameSpace the namespace of the object to be deleted.
     * @param name      the name of the object to be deleted.
     */
    void remove(final String nameSpace, final String name);

    /**
     * Remove the object specified by the uid.
     *
     * @param uid the uid of the object to be removed.
     */
    void remove(final UUID uid);
}
