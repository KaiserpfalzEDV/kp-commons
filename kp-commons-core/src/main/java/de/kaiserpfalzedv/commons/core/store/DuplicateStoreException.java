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

package de.kaiserpfalzedv.commons.core.store;

import de.kaiserpfalzedv.commons.core.api.BaseSystemException;
import de.kaiserpfalzedv.commons.core.resources.ResourcePointer;

/**
 * DuplicateStoreException -- There is already an object with this metadata.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0  2021-01-31
 */
public class DuplicateStoreException extends BaseSystemException {
    private final ResourcePointer stored;
    private final ResourcePointer duplicate;

    /**
     * @param stored    the already stored resource metadata.
     * @param duplicate the new resource metadata.
     * @since 1.2.0
     */
    public DuplicateStoreException(final ResourcePointer stored, final ResourcePointer duplicate) {
        super(String.format("Duplicate element found. resource='%s', nameSpace='%s', name='%s'",
                stored.getKind(), stored.getNamespace(), stored.getName()
        ));

        this.stored = stored;
        this.duplicate = duplicate;
    }

    /**
     * @return the generation stored in the data store.
     */
    @SuppressWarnings("unused")
    public ResourcePointer getStoredGeneration() {
        return stored;
    }

    /**
     * @return the generation that should be saved.
     */
    @SuppressWarnings("unused")
    public ResourcePointer getSaveGeneration() {
        return duplicate;
    }
}
