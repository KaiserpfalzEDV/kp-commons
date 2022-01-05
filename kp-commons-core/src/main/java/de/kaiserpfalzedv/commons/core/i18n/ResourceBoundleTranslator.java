/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.i18n;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * Translator -- Provides a nice way to read translations from Resource bundles.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
@Singleton
@Slf4j
public class ResourceBoundleTranslator implements Translator, MessageSource {
    /**
     * The languages this class provides
     */
    private static final List<Locale> PROVIDED_LANGUAGES = Arrays.asList(Locale.GERMAN, Locale.ENGLISH);

    /**
     * Default bundle to use when no other bundle is selected.
     */
    private final String defaultBundle;

    @ConfigProperty(name = "quarkus.default-locale", defaultValue = "de")
    private String defaultLocale;

    private final HashMap<String, HashMap<Locale, ResourceBundle>> bundles = new HashMap<>();


    public ResourceBoundleTranslator() {
        this("messages");
    }

    public ResourceBoundleTranslator(@NotNull final String defaultBundle) {
        this.defaultBundle = defaultBundle;
    }


    @Override
    public String getMessage(String key, @NotNull Object[] params, Locale locale) throws NoSuchMessageException {
        return getTranslation(key, locale, params);
    }

    @Override
    public String getTranslation(final String key, final Locale locale, Object... arguments) {
        return getTranslation(defaultBundle, key, locale, arguments);
    }

    @Override
    public String getTranslation(final Object bundleObject, final String key, final Locale locale, final Object... arguments) {
        String bundleName = bundleObject.getClass().getCanonicalName().replace(".", "/");

        return getTranslation(bundleName, key, locale, arguments);
    }

    @Override
    public String getTranslation(final String bundleName, final String key, final Locale locale, Object... arguments) {
        loadBundle(bundleName, locale);

        try {
            final String pattern = bundles.get(bundleName).get(locale).getString(key);
            final MessageFormat format = new MessageFormat(pattern, locale);
            return format.format(arguments);
        } catch (NullPointerException | MissingResourceException ex) {
            log.warn(
                    "Translation failed. bundle={}, locale={}, key={}",
                    bundleName, locale, key
            );
            return "!" + key;
        }
    }



    /**
     * Loads the bundle into the cache.
     *
     * @param bundleName The base filename for the translation bundle.
     * @param locale     The locale to load the bundle for.
     */
    private void loadBundle(@NotNull String bundleName, @NotNull Locale locale) {
        if (!bundles.containsKey(bundleName)) {
            log.debug("Adding bundle. baseName='{}'", bundleName);

            bundles.put(bundleName, new HashMap<>());
        }

        if (locale == null) {
            locale = Locale.forLanguageTag(defaultLocale);
        }

        if (!bundles.get(bundleName).containsKey(locale)) {
            log.info("Loading bundle. baseName='{}', locale='{}'", bundleName, locale.getDisplayName());

            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle(bundleName, locale, new UnicodeResourceBundleControl());
            } catch (NullPointerException | MissingResourceException e) {
                Locale l = Locale.forLanguageTag(locale.getLanguage());

                log.warn("Translator did not find the wanted locale for the bundle. bundle={}, locale={}, orig.locale={}",
                        bundleName, l, locale);
                try {
                    bundle = ResourceBundle.getBundle(bundleName, l, new UnicodeResourceBundleControl());
                } catch (NullPointerException | MissingResourceException e1) {
                    log.warn("Translator did not find the wanted bundle. Using default bundle. bundle={}", bundleName);

                    try {
                        bundle = ResourceBundle.getBundle(defaultBundle, Locale.forLanguageTag(defaultLocale),
                                new UnicodeResourceBundleControl());
                    } catch (NullPointerException e2) {
                        log.error("Resource bundle can't be read.", e2);

                        return;
                    }
                }
            }
            bundles.get(bundleName).put(locale, bundle);
        }
    }

    @Override
    public void close() throws Exception {
        log.info("Closing all bundles.");
        bundles.clear();
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return PROVIDED_LANGUAGES;
    }

    /**
     * @author peholmst
     * @since 0.1.0
     */
    private static class UnicodeResourceBundleControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(
                final String baseName,
                final Locale locale,
                final String format,
                final ClassLoader loader,
                final boolean reload
        ) throws IOException {

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            final URL resourceURL = loader.getResource(resourceName);
            if (resourceURL == null)
                return null;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                return new PropertyResourceBundle(in);
            }
        }
    }

}
