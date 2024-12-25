/*
 * Copyright (c) 2023 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.kaiserpfalzedv.commons.spring.jackson;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

/**
 * Enables the usage of {@link Autowired} in Json objects.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-08
 */
@Configuration
@XSlf4j
public class JsonAutowiringConfiguration {
    /** @return the Jackson2ObjectMapperBuilder that enables the {@link Autowired} annotation in Jackson deserialized objects. */
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder(final HandlerInstantiator handlerInstantiator) {
        log.entry(handlerInstantiator);

        final Jackson2ObjectMapperBuilder result = new Jackson2ObjectMapperBuilder();
        result.handlerInstantiator(handlerInstantiator);

        return log.exit(result);
    }


    /** @return The HandlerInstantiator needed by the {@link #objectMapperBuilder(HandlerInstantiator)} */
    @Bean
    public HandlerInstantiator handlerInstantiator(final ApplicationContext applicationContext) {
        return log.exit(new SpringHandlerInstantiator(applicationContext.getAutowireCapableBeanFactory()));
    }
}
