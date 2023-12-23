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

package de.kaiserpfalzedv.commons.api.store;

import de.kaiserpfalzedv.commons.api.BaseSystemException;
import de.kaiserpfalzedv.commons.api.resources.Pointer;

/**
 * DuplicateStoreException -- There is already an object with this metadata.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public class DuplicateStoreException extends BaseSystemException {
    private final Pointer stored;
    private final Pointer duplicate;

    /**
     * @param stored    the already stored resource metadata.
     * @param duplicate the new resource metadata.
     * @since 2.0.0  2021-05-24
     */
    public DuplicateStoreException(final Pointer stored, final Pointer duplicate) {
        super(String.format("Duplicate element found. resource='%s', nameSpace='%s', name='%s'",
                stored.getKind(), stored.getNameSpace(), stored.getName()
        ));

        this.stored = stored;
        this.duplicate = duplicate;
    }

    /**
     * @return the generation stored in the data store.
     */
    public Pointer getStoredGeneration() {
        return this.stored;
    }

    /**
     * @return the generation that should be saved.
     */
    public Pointer getSaveGeneration() {
        return this.duplicate;
    }
}
