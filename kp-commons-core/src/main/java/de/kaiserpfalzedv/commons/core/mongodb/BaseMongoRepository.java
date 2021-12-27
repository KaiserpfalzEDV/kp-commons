/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.core.mongodb;

import de.kaiserpfalzedv.commons.core.resources.Resource;
import de.kaiserpfalzedv.commons.core.store.OptimisticLockStoreException;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * BaseMongoRepository -- The base methods for all stores.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public interface BaseMongoRepository<T extends Resource<D>, D extends Serializable> extends PanacheMongoRepositoryBase<T, UUID> {
    default T prepareStorage(T object) throws OptimisticLockStoreException {
        Optional<T> stored = findByIdOptional(object.getUid());

        stored.ifPresentOrElse(
                t -> {
                    if (Objects.equals(t.getGeneration(), object.getGeneration())) {
                        object.increaseGeneration();

                    } else if (t.getGeneration() > object.getGeneration()) {
                        throw new OptimisticLockStoreException(t.getGeneration(), object.getGeneration());
                    }
                },
                () -> {}
        );

        return object;
    }
}
