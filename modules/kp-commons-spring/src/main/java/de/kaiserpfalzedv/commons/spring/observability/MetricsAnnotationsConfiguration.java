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
package de.kaiserpfalzedv.commons.spring.observability;


import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Provides the timed and the counted aspect for observabilities.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 4.0.0  2024-09-22
 * @since 1.0.0  2023-12-10
 */
@Configuration
@XSlf4j
public class MetricsAnnotationsConfiguration {
    @Bean
    public TimedAspect timedAspect(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final MeterRegistry registry) {
        log.entry(registry);

        return log.exit(new TimedAspect(registry));
    }

    @Bean
    public CountedAspect countedAspect(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final MeterRegistry registry) {
        log.entry(registry);

        return log.exit(new CountedAspect(registry));
    }
}
