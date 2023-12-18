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

package de.kaiserpfalzedv.commons.api.libravatar;

/**
 * <p>Avatar -- A single avatar to be displayed.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-19
 */
public interface Avatar {
    /***
     *
     * @return The Libravatar itself
     */
    byte[] download(final AvatarOptions options);

    /**
     * Builds the URI according to set options and the given email Address.
     *
     * @return The URI for retrieving the Libravatar.
     */
    String buildUrl(final AvatarOptions options);

    String getEmail();
}
