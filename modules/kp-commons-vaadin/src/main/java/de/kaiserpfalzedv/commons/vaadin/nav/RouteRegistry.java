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

import com.vaadin.flow.component.Component;
import com.vaadin.quarkus.annotation.NormalUIScoped;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * <p>RouteRegistry -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-20
 */
@RegisterForReflection
@Unremovable
@NormalUIScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class RouteRegistry implements Serializable, AutoCloseable {
    private final ConcurrentSkipListSet<Component> components = new ConcurrentSkipListSet<>();

    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void close() {
    }
}
