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

package de.kaiserpfalzedv.commons.core.store;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import de.kaiserpfalzedv.commons.api.resources.Resource;
import de.kaiserpfalzedv.commons.api.store.OptimisticLockStoreException;
import de.kaiserpfalzedv.commons.api.store.StoreService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * GenericStoreService -- an ephemeral store for Resources.
 * <p>
 * This is a memory alternative for a persistent data store.
 *
 * @param <T> The resource to be stored inside the data store.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@ToString
@EqualsAndHashCode
@Slf4j
public abstract class GenericStoreService<T extends Resource<?>> implements StoreService<T> {
    /**
     * The name based memory store for guilds.
     */
    private final HashMap<String, T> namedStore = new HashMap<>(10);

    /**
     * The uid based memory store for guilds.
     */
    private final HashMap<UUID, T> uidStore = new HashMap<>(10);


    @Override
    public Optional<T> findByNameSpaceAndName(final String nameSpace, final String name) {
        final String key = this.generateStoreKey(nameSpace, name);

        return Optional.ofNullable(this.namedStore.get(key));
    }

    @Override
    public Optional<T> findByUid(final UUID uid) {
        return Optional.ofNullable(this.uidStore.get(uid));
    }

    private String generateStoreKey(final String nameSpace, final String name) {
        return nameSpace + "-" + name;
    }

    @Override
    public T save(final T object) throws OptimisticLockStoreException {
        log.trace("Saving: {}", object);

        final String key = this.generateStoreKey(object.getNameSpace(), object.getName());

        @SuppressWarnings("unchecked")
        final
        T data = (!this.namedStore.containsKey(key))
                ? object
                : (T) object.increaseGeneration();


        this.checkOptimisticLocking(key, data);

        this.namedStore.put(key, data);
        this.uidStore.put(object.getUid(), data);
        return data;
    }

    private void checkOptimisticLocking(final String key, final T object) {
        if (
                this.namedStore.containsKey(key)
                        && (this.namedStore.get(key).getGeneration() >= object.getGeneration())
        ) {
            throw new OptimisticLockStoreException(this.namedStore.get(key).getGeneration(), object.getGeneration());
        }
    }

    @Override
    public void remove(final T object) {
        final String key = this.generateStoreKey(object.getNameSpace(), object.getName());

        this.namedStore.remove(key);
        this.uidStore.remove(object.getUid());
    }

    @Override
    public void remove(final String nameSpace, final String name) {
        final String key = this.generateStoreKey(nameSpace, name);

        if (this.namedStore.containsKey(key)) {
            this.uidStore.remove(this.namedStore.get(key).getUid());
            this.namedStore.remove(key);
        }
    }

    @Override
    public void remove(final UUID uid) {
        if (this.uidStore.containsKey(uid)) {
            final T data = this.uidStore.get(uid);
            final String key = this.generateStoreKey(data.getNameSpace(), data.getName());

            this.namedStore.remove(key);
            this.uidStore.remove(uid);
        }
    }
}
