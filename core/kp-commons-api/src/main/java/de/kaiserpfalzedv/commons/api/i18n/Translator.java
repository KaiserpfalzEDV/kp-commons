/*
 * Copyright (c) 2021-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.api.i18n;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * @author rlichti
 * @version 1.0.0 2021-09-09
 * @since 1.0.0 2021-09-09
 */
public interface Translator extends Serializable, AutoCloseable {

    /**
     * Reads the translation from the default bundle
     *
     * @param key       The key of the bundle entry.
     * @param locale    the locale to use.
     * @param arguments Arguments for the translation.
     * @return The translated text or {@literal !<key>}
     */
    String getTranslation(String key, Locale locale, Object... arguments);

    /**
     * Returns the translation from a resource accompanying the class given as bundleObject.
     *
     * @param bundleObject The class to be translated.
     * @param key          The key for the translation.
     * @param locale       The locale to use.
     * @param arguments    Arguments to the translation.
     * @return The translated string or {@literal !<key>}.
     */
    String getTranslation(Object bundleObject, String key, Locale locale, Object... arguments);

    /**
     * Reads the translation from the bundle with the given name.
     *
     * @param bundleName The name of the bundle. The locale and the postfix {@literal .properties} will be appended.
     * @param key        The key of the bundle entry.
     * @param locale     the locale to use.
     * @param arguments  Arguments for the translation.
     * @return The translated text or {@literal !<key>}
     */
    String getTranslation(String bundleName, String key, Locale locale, Object... arguments);

    List<Locale> getProvidedLocales();
}
