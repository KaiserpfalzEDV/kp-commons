/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.core.i18n;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * TranslatorTest -- Tests the common translator.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-08
 */
@Slf4j
class TranslatorTest extends AbstractTestBase {
    private static final List<Locale> supportedLocales = Arrays.asList(
            Locale.forLanguageTag("de"),
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("fr"),
            Locale.forLanguageTag("nl"),
            Locale.forLanguageTag("es")
    );
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("de");


    /**
     * Service under test.
     */
    private ResourceBundleTranslator sut;


    public TranslatorTest() {

        setTestSuite(getClass().getSimpleName());
        setLog(log);
    }


    @Test
    void shouldReturnListOfSupportedLocales() {
        startTest("supported-locales");

        List<Locale> result = sut.getProvidedLocales();
        log.debug("result: {}", result.toArray());

        assertArrayEquals(
                supportedLocales.toArray(new Locale[]{}),
                result.toArray(new Locale[]{}),
                "The supported locals do not equal the expected ones.");
    }

    @Test
    void shouldReturnTheGermanTranslationWhenCalledWithAnExistingElementFromTheDefaultBundle() {
        startTest("valid-german-translation");

        String result = sut.getTranslation("existing.no-params", DEFAULT_LOCALE);
        log.debug("result: {}", result);

        assertEquals("Dies ist ein Teststring.", result);
    }

    @Test
    void shouldReturnTheTranslationWhenCalledWithAnExistingElementFromTheClassLocalBundle() {
        startTest("valid-german-translation-class-bundle");

        String result = sut.getTranslation(this, "existing.no-params", DEFAULT_LOCALE);
        log.debug("result: {}", result);

        assertEquals("Dies ist ein Teststring aus TranslatorTest.", result);
    }


    @Test
    void shouldReturnAnErrorCodedStringWhenCalledWithAnNonExistingElementFromTheDefaultBundle() {
        startTest("missing-german-translation");

        String result = sut.getTranslation("non-existing", DEFAULT_LOCALE);
        log.debug("result: {}", result);

        assertEquals("!non-existing", result);
    }

    @Test
    void shouldReturnTheStringWithParametersReplacedWhenCalledWithParameters() {
        startTest("adding-parameters-to-translation");

        UUID uuid = UUID.randomUUID();
        String expected = String.format("Dies ist ein Teststring mit 1 Parameter: 0='%s'", uuid);

        String result = sut.getTranslation("existing.with-param", DEFAULT_LOCALE, uuid);
        log.debug("result: {}", result);

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnTheStringWithParametersReplacedWhenCalledWithTooFewParameters() {
        startTest("adding-too-few-parameters-to-translation");

        UUID uuid = UUID.randomUUID();
        String expected = String.format("Dieser String hat 2 Parameter: 0='%s', 1='{1}'.", uuid);

        String result = sut.getTranslation("existing.with-params", DEFAULT_LOCALE, uuid);
        log.debug("result: {}", result);

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnTheStringWithTwoParametersReplacedWhenAllParametersAreGiven() {
        startTest("adding-two-parameters-to-translation");

        UUID uuid = UUID.randomUUID();
        String param2 = "Und ein String";

        String expected = String.format("Dieser String hat 2 Parameter: 0='%s', 1='%s'.", uuid, param2);

        String result = sut.getTranslation("existing.with-params", DEFAULT_LOCALE, uuid, param2);
        log.debug("result: {}", result);

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnTheEnglishTranslationWhenCalledWithAnExistingElementFromTheDefaultBundle() {
        startTest("valid-english-translation");

        String result = sut.getTranslation("existing.no-params", Locale.ENGLISH);
        log.debug("result: {}", result);

        assertEquals("This is a string to test.", result);
    }

    @Test
    void shouldFailWhenANonExistingMessageBundleIsSpecified() {
        startTest("non-existing-bundle");

        String result = sut.getTranslation("ignored", "ignored", Locale.GERMAN);
        log.debug("result: {}", result);

        assertEquals("!ignored", result);
    }


    @Test
    void close() throws Exception {
        try {
            sut.close();
        } catch (RuntimeException e) {
            fail("close() should never throw an exception ...");
        }
    }


    @BeforeEach
    void setUp() {
        sut = new ResourceBundleTranslator("test-messages");

        sut.setDefaultLocale(DEFAULT_LOCALE.getLanguage());
        sut.setConfiguredLanguages(Arrays.asList("de", "en", "fr", "nl", "es"));
        log.trace("Supported locales. default='{}', list={}", sut.getDefaultLocale(), sut.getConfiguredLanguages());
    }
}