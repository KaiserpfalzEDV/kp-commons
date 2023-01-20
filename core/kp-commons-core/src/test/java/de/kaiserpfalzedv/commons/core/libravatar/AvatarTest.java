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

import de.kaiserpfalzedv.commons.api.libravatar.LibravatarDefaultImage;
import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>AvatarTest -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
@Slf4j
public class AvatarTest extends AbstractTestBase {
    private static final String DEFAULT_EMAIL = "support@kaiserpfalz-edv.de";
    private static final TestAvatarOptions DEFAULT_OPTIONS = TestAvatarOptions.builder().build();

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

    private Avatar sut;

    public AvatarTest() {
        setTestSuite(getClass().getSimpleName());
        setLog(log);
    }

    @Test
    void shouldReturnTheHttpsUriWhenUsingDefaultConfiguration() {
        startTest("https", DEFAULT_EMAIL, DEFAULT_OPTIONS);

        String result = sut.buildUrl(DEFAULT_OPTIONS);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_URI, result);
    }

    @Test
    void shouldReturnTheHttpsUriWithSize100WhenUsingTheImageSize100() {
        TestAvatarOptions options = DEFAULT_OPTIONS.toBuilder().imageSize(100).build();
        startTest("https-100", DEFAULT_EMAIL, options);

        String result = sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_100_URI, result);
    }


    @Test
    void shouldReturnTheHttpsUriWithRetroDefaultWhenUsingTheRetroDefaultIsSelected() {
        TestAvatarOptions options = DEFAULT_OPTIONS.toBuilder().defaultImage(LibravatarDefaultImage.RETRO).build();
        startTest("https-retro", DEFAULT_EMAIL, options);

        String result = sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_RETRO_URI, result);
    }


    @Test
    void shouldReturnTheHttpUriWhenUsingHttpInsteadOfHttps() {
        TestAvatarOptions options = DEFAULT_OPTIONS.toBuilder().useHttps(false).build();
        startTest("http", DEFAULT_EMAIL, options);

        String result = sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTP_MD5_URI, result);
    }


    @Test
    void shouldReturnTheHttpUriWithoutDefaultConfigUsingWhenDefaultImageIsConfiguredAsDefault() {
        TestAvatarOptions options = DEFAULT_OPTIONS.toBuilder()
                .useHttps(false)
                .defaultImage(LibravatarDefaultImage.DEFAULT)
                .build();
        startTest("http-default-image", DEFAULT_EMAIL, options);

        String result = sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTP_MD5_DEFAULT_URI, result);
    }
    @Test
    void shouldReturnSha256UriWhenUsingSha256() {
        TestAvatarOptions options = DEFAULT_OPTIONS.toBuilder().useSHA256(true).build();
        startTest("https-sha256", DEFAULT_EMAIL, options);

        String result = sut.buildUrl(options);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_SHA256_URI, result);
    }


    @BeforeEach
    void setUp() {
        sut = new Avatar(DEFAULT_EMAIL);
    }
}
