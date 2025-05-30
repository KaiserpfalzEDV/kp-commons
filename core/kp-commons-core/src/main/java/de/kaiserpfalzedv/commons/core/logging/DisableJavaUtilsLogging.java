/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.core.logging;


import jakarta.annotation.PreDestroy;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-30
 */
@Component
@ToString
@XSlf4j
public class DisableJavaUtilsLogging implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(@NonNull ApplicationStartingEvent event) {
        log.info("Deaktiviere Java Utils Logging und installiere SLF4J Bridge");
        
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
    
    @PreDestroy
    public void close() {
        log.entry();

        if (SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.uninstall();
        }
        
        log.exit();
    }
}