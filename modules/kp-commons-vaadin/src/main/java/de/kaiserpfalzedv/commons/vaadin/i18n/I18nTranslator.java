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

package de.kaiserpfalzedv.commons.vaadin.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import de.kaiserpfalzedv.commons.core.i18n.ResourceBundleTranslator;
import io.quarkus.arc.Unremovable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Locale;


@Unremovable
@Default
@VaadinServiceEnabled
@VaadinServiceScoped
@Slf4j
public class I18nTranslator extends ResourceBundleTranslator implements I18NProvider {
    private static final String DEFAULT_LOCALE_CONFIG_ITEM = "quarkus.default-locale";
    private static final String DEFAULT_LOCALE = "de";


    private final String defaultLocale;

    /**
     * The locale. Unless it is set via {@link #setLocale(Locale)} it will default to the locale configured by
     * {@link #defaultLocale} (which uses the configuration item {@value #DEFAULT_LOCALE_CONFIG_ITEM}) and if that is
     * not configured, it falls back to {@value #DEFAULT_LOCALE} ({@link #DEFAULT_LOCALE}).
     */
    @Produces
    @Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    private Locale locale;


    @Inject
    public I18nTranslator(
            @ConfigProperty(name = DEFAULT_LOCALE_CONFIG_ITEM, defaultValue = DEFAULT_LOCALE)
            String defaultLocale
    ) {
        super("/messages/msg");

        this.defaultLocale = defaultLocale;
        this.locale = Locale.forLanguageTag(defaultLocale);
    }

    public I18nTranslator() {
        this(DEFAULT_LOCALE);
    }

    /**
     * Returns the translation key. Since the view names are the original view names suffixed with "_Subclass" we replace
     * "_Subclass" with "" to counteract.
     *
     * @param key       The key of the bundle entry.
     * @param locale    the locale to use.
     * @param params Arguments for the translation.
     * @return The translation or "!{key}" if there is no translation.
     */
    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return super.getTranslation(key.replace("_Subclass", ""), locale, params);
    }
}
