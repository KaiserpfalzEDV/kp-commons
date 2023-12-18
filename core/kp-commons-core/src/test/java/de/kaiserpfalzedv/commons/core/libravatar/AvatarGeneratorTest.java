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

import de.kaiserpfalzedv.commons.api.libravatar.AvatarOptions;
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
public class AvatarGeneratorTest extends AbstractTestBase {
    private static final String DEFAULT_EMAIL = "support@kaiserpfalz-edv.de";
    private static final AvatarOptions DEFAULT_OPTIONS = AvatarOptions.builder().build();
    private static final String DEFAULT_HTTPS_MD5_URI =
            "https://seccdn.libravatar.org/avatar/1798b62948dc167ccab6d89b8f4d3e82"
            + "?s=" + DEFAULT_OPTIONS.imageSize()
            + "&d=" + DEFAULT_OPTIONS.defaultImage().getCode();

    private AvatarGenerator sut;

    public AvatarGeneratorTest() {
        setTestSuite(getClass().getSimpleName());
        setLog(log);
    }

    @Test
    void shouldGenerateCorrectAvatarWithDefaultConfiruation() {
        startTest("correct-avatar", DEFAULT_EMAIL, DEFAULT_OPTIONS);

        String result = sut.generateUri(DEFAULT_EMAIL);
        log.debug("result. data='{}'", result);

        Assertions.assertEquals(DEFAULT_HTTPS_MD5_URI, result);
    }


    @BeforeEach
    void setUp() {
        sut = new AvatarGenerator(DEFAULT_OPTIONS);
    }
}
