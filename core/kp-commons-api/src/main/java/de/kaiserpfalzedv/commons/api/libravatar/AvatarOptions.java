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

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>AvatarOptions -- The configuration for libravatar.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-19
 */
@ConfigurationProperties("libravatar")
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@Data
@Accessors(fluent = true, chain = true)
public class AvatarOptions implements Serializable {
	private static final long serialVersionUID = 0L;

	/**
	 * Specifies a custom base URI for HTTP use. The default is to use the
	 * official libravatar HTTP server. If you *really* wanted to use a non-free
	 * server, you could set this to "http://gravatar.com/avatar/", but why
	 * would you do such a thing?
	 */
    @Builder.Default
    private String baseUri = "http://cdn.libravatar.org/avatar/";

	/**
	 * Specifies a custom base URI for HTTPS use. The default is to use the
	 * official libravatar HTTPS server.
	 */
    @Builder.Default
    private String secureBaseUri = "https://seccdn.libravatar.org/avatar/";

	/**
	 * Produce https:// URIs where possible. This avoids mixed-content warnings
	 * in browsers when using libravatar-sharp from within a page served via
	 * HTTPS.
	 **/
    @Builder.Default
    private boolean useHttps = true;

	/**
	 * Use the SHA256 hash algorithm, rather than MD5. SHA256 is significantly
	 * stronger, but is not supported by Gravatar, so libravatar's fallback to
	 * Gravatar for missing images will not work. Note that using
	 * Avatar.FromOpenID implicitly uses SHA256.
	 */
    @Builder.Default
    private boolean useSHA256 = false;

	/**
	 * URI for a default image, if no image is found for the user. This also
	 * accepts any of the "special" values in AvatarDefaultImages
	 */
    @Builder.Default
    private LibravatarDefaultImage defaultImage =  LibravatarDefaultImage.IDENTICON;

    /**
	 * Size of the image requested. Valid values are between 1 and 512 pixels.
	 * The default size is 80 pixels.
	 */
    @Builder.Default
    private Integer imageSize = 80;
}
