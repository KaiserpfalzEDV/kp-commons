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

package de.kaiserpfalzedv.commons.vaadin.nav;

import lombok.Getter;
import lombok.experimental.Accessors;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * <p>AppNavRoute -- The defintion of a route element.</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-20
 */
@Qualifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppNavRoute {
    int DEFAULT_APP_NAV_PRIORITY = 100;

    @Nonbinding
    String label() default "No Menu Text defined";

    @Nonbinding
    String i18n() default "";

    @Nonbinding
    String icon() default "";



    @Getter
    @Accessors(fluent = true)
    final class Literal extends AnnotationLiteral<AppNavRoute> implements AppNavRoute {
        private String label;
        private String i18n;
        private String icon;
    }
}
