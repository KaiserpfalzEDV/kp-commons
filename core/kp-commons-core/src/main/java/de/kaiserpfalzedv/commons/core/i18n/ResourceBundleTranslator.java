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

package de.kaiserpfalzedv.commons.core.i18n;

import de.kaiserpfalzedv.commons.api.i18n.MessageSource;
import de.kaiserpfalzedv.commons.api.i18n.NoSuchMessageException;
import de.kaiserpfalzedv.commons.api.i18n.Translator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Translator -- Provides a nice way to read translations from Resource bundles.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
@Slf4j
public class ResourceBundleTranslator implements Translator, MessageSource {
    /**
     * The languages this class provides. To enable testing, a setter is provided. Normally it will be configured via
     * property with a default of "de,en,fr,nl,es,it".
     */
    @Setter
    List<String> configuredLanguages = List.of("de","en","fr","nl","es","it");

    /**
     * Default bundle to use when no other bundle is selected.
     */
    private final String defaultBundle;

    /**
     * The default locale (used when no locale is specified in the translation call). To enable testing, a setter is
     * provided. Normally it will be configured via property "default-locale" with a default of "de".
     */
    @Setter
    String defaultLocale = "de";

    private final HashMap<String, HashMap<Locale, ResourceBundle>> bundles = new HashMap<>();


    public ResourceBundleTranslator() {
        this("messages");
    }

    public ResourceBundleTranslator(final String defaultBundle) {
        this.defaultBundle = defaultBundle;
    }


    @SuppressWarnings("RedundantThrows")
    @Override
    public String getMessage(String key, Object[] params, Locale locale) throws NoSuchMessageException {
        return getTranslation(key, locale, params);
    }

    @Override
    public String getTranslation(final String key, final Locale locale, Object... arguments) {
        return getTranslation(defaultBundle, key, locale, arguments);
    }

    @Override
    public String getTranslation(final Object bundleObject, final String key, final Locale locale, final Object... arguments) {
        String bundleName = bundleObject.getClass().getCanonicalName()
                .replace(".", "/")
                .replace("_Subclass", ""); // get around for lombok or both or so. :-(

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
    private void loadBundle(String bundleName, Locale locale) {
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

                log.warn("Translator did not find the wanted locale for the bundle. bundle={}, locale={}, orig.locale={}, error='{}'",
                        bundleName, l, locale, e.getMessage());
                try {
                    bundle = ResourceBundle.getBundle(bundleName, l, new UnicodeResourceBundleControl());
                } catch (NullPointerException | MissingResourceException e1) {
                    log.warn("Translator did not find the wanted bundle. Using default bundle. bundle={}, error='{}'", bundleName, e1.getMessage());

                    try {
                        bundle = ResourceBundle.getBundle(defaultBundle, Locale.forLanguageTag(defaultLocale),
                                new UnicodeResourceBundleControl());
                    } catch (NullPointerException | MissingResourceException e2) {
                        log.error("Resource bundle can't be read. error='{}'", e2.getMessage());

                        return;
                    }
                }
            }
            bundles.get(bundleName).put(locale, bundle);
        }
    }

    @Override
    public void close() {
        log.info("Closing all bundles.");
        bundles.clear();
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return configuredLanguages.stream()
                .map(Locale::forLanguageTag)
                .filter(d -> {log.trace("Mapped language. locale={}", d); return true;})
                .collect(Collectors.toList());
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
            ClassLoader used = Thread.currentThread().getContextClassLoader();

            log.debug("Classloader will be ignored. used={}, ignored={}", used, loader);

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            final URL resourceURL = used.getResource(resourceName);
            if (resourceURL == null)
                return null;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                return new PropertyResourceBundle(in);
            }
        }
    }
}
