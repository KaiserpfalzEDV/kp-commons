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

import de.kaiserpfalzedv.commons.api.BaseSystemException;

/**
 * OptimisticLockStoreException -- The data has been changed.
 * <p>
 * If the generation does not match (a newer generation is already in data store) then this exception is thrown.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public class OptimisticLockStoreException extends BaseSystemException {
    private final long storedGeneration;
    private final long saveGeneration;

    /**
     * @param storedGeneration the generation already in store.
     * @param saveGeneration the generation to save.
     * @since 2.0.0  2021-05-24
     */
    public OptimisticLockStoreException(final long storedGeneration, final long saveGeneration) {
        super(String.format("Tried to save generation '%d'. But generation '%d' already in store.",
                saveGeneration, storedGeneration));

        this.storedGeneration = storedGeneration;
        this.saveGeneration = saveGeneration;
    }

    /**
     * @return the generation stored in the data store.
     */
    public long getStoredGeneration() {
        return this.storedGeneration;
    }

    /**
     * @return the generation that should be saved.
     */
    public long getSaveGeneration() {
        return this.saveGeneration;
    }
}
