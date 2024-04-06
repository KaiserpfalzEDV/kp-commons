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

package de.kaiserpfalzedv.commons.core.libravatar;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import de.kaiserpfalzedv.commons.api.libravatar.AvatarOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 * <p>AvatarGenerator -- The service to generate libravatars with.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
@ApplicationScoped
@EnableConfigurationProperties(AvatarOptions.class)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AvatarGenerator {
    private final AvatarOptions options;

    public String generateUri(final String email) {
        return new AvatarImpl(email).buildUrl(this.options);
    }

    public byte[] download(final String email) {
        return new AvatarImpl(email).download(this.options);
    }
}
