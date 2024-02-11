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

/*
 * Libravatar -- Java Library for retrieving libravatars.
 *
 * The code in this package is taken from {@linkplain https://github.com/alessandroleite/libravatar-j}.
 *
 * It is licensed under a MIT license by Alessandro Leite.
 */
package de.kaiserpfalzedv.commons.core.libravatar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import de.kaiserpfalzedv.commons.api.libravatar.AvatarException;
import de.kaiserpfalzedv.commons.api.libravatar.AvatarOptions;
import de.kaiserpfalzedv.commons.api.libravatar.LibravatarDefaultImage;
import jakarta.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true, fluent = false)
@Slf4j
public class Avatar implements de.kaiserpfalzedv.commons.api.libravatar.Avatar {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    private final String email;

    @Override
    public byte[] download(final de.kaiserpfalzedv.commons.api.libravatar.AvatarOptions options) {
        try (InputStream is = URL.of(URI.create(this.buildUrl(options)), null).openStream()) {
            return IOUtils.toByteArray(is);
        } catch (final IOException e) {
            throw new AvatarException(e);
        }
    }

    @Override
    public String buildUrl(final AvatarOptions options) {
        log.debug("avatar build. https={}, sha256={}, size={}",
                options.useHttps(), options.useSHA256(), options.imageSize());

        final StringBuilder sb = new StringBuilder(
                options.useHttps() ? options.secureBaseUri()
                        : options.baseUri());

        sb.append(
                options.useSHA256() ? DigestUtils.sha256Hex(this.email.toLowerCase().getBytes())
                        : DigestUtils.md5Hex(this.email.toLowerCase().getBytes()));

        sb.append("?s=").append(options.imageSize());

        sb.append(
                options.defaultImage() != LibravatarDefaultImage.DEFAULT ? "&d=" + options.defaultImage().getCode()
                        : "");

        return sb.toString();
    }
}