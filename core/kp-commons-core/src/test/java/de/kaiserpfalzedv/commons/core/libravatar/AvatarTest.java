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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.kaiserpfalzedv.commons.api.libravatar.AvatarOptions;
import de.kaiserpfalzedv.commons.api.libravatar.LibravatarDefaultImage;
import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>AvatarTest -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
@Slf4j
public class AvatarTest extends AbstractTestBase {
    private static final String DEFAULT_EMAIL = "support@kaiserpfalz-edv.de";
    private static final AvatarOptions DEFAULT_OPTIONS = AvatarOptions.builder().build();

    private static final String DEFAULT_HTTPS_MD5_URI = "https://seccdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=" + DEFAULT_OPTIONS.imageSize()
            + "&d=" + DEFAULT_OPTIONS.defaultImage().getCode();
    private static final String DEFAULT_HTTPS_MD5_RETRO_URI = "https://seccdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=" + DEFAULT_OPTIONS.imageSize()
            + "&d=retro";
    private static final String DEFAULT_HTTPS_MD5_100_URI = "https://seccdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=100"
            + "&d=" + DEFAULT_OPTIONS.defaultImage().getCode();

    private static final String DEFAULT_HTTPS_SHA256_URI = "https://seccdn.libravatar.org/avatar/291a67cbb1c9dd3db384dc0fe6bd00671ec128278e5b9772e0049a770b8928df"
            + "?s=" + DEFAULT_OPTIONS.imageSize()
            + "&d=" + DEFAULT_OPTIONS.defaultImage().getCode();
    @SuppressWarnings("HttpUrlsUsage")
    private static final String DEFAULT_HTTP_MD5_URI = "http://cdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=" + DEFAULT_OPTIONS.imageSize()
            + "&d=" + DEFAULT_OPTIONS.defaultImage().getCode();

    @SuppressWarnings("HttpUrlsUsage")
    private static final String DEFAULT_HTTP_MD5_DEFAULT_URI = "http://cdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=" + DEFAULT_OPTIONS.imageSize();

    private AvatarImpl sut;

    public AvatarTest() {
        this.setTestSuite(this.getClass().getSimpleName());
        this.setLog(log);
    }

    @Test
    void shouldReturnTheHttpsUriWhenUsingDefaultConfiguration() {
        this.startTest("https", DEFAULT_EMAIL, DEFAULT_OPTIONS);

        final String result = this.sut.buildUrl(DEFAULT_OPTIONS);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_URI, result);
    }

    @Test
    void shouldReturnTheHttpsUriWithSize100WhenUsingTheImageSize100() {
        final AvatarOptions options = DEFAULT_OPTIONS.toBuilder().imageSize(100).build();
        this.startTest("https-100", DEFAULT_EMAIL, options);

        final String result = this.sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_100_URI, result);
    }


    @Test
    void shouldReturnTheHttpsUriWithRetroDefaultWhenUsingTheRetroDefaultIsSelected() {
        final AvatarOptions options = DEFAULT_OPTIONS.toBuilder().defaultImage(LibravatarDefaultImage.RETRO).build();
        this.startTest("https-retro", DEFAULT_EMAIL, options);

        final String result = this.sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_RETRO_URI, result);
    }


    @Test
    void shouldReturnTheHttpUriWhenUsingHttpInsteadOfHttps() {
        final AvatarOptions options = DEFAULT_OPTIONS.toBuilder().useHttps(false).build();
        this.startTest("http", DEFAULT_EMAIL, options);

        final String result = this.sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTP_MD5_URI, result);
    }


    @Test
    void shouldReturnTheHttpUriWithoutDefaultConfigUsingWhenDefaultImageIsConfiguredAsDefault() {
        final AvatarOptions options = DEFAULT_OPTIONS.toBuilder()
                .useHttps(false)
                .defaultImage(LibravatarDefaultImage.DEFAULT)
                .build();
        this.startTest("http-default-image", DEFAULT_EMAIL, options);

        final String result = this.sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTP_MD5_DEFAULT_URI, result);
    }
    @Test
    void shouldReturnSha256UriWhenUsingSha256() {
        final AvatarOptions options = DEFAULT_OPTIONS.toBuilder().useSHA256(true).build();
        this.startTest("https-sha256", DEFAULT_EMAIL, options);

        final String result = this.sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_SHA256_URI, result);
    }


    @BeforeEach
    void setUp() {
        this.sut = new AvatarImpl(DEFAULT_EMAIL);
    }
}
