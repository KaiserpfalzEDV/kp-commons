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

package de.kaiserpfalzedv.commons.core.workflow.rest;

import de.kaiserpfalzedv.commons.core.workflow.WorkflowFilter;
import de.kaiserpfalzedv.commons.core.workflow.WorkflowInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

/**
 * WorkflowProvider -- Provides the workflow data of the current request.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-04
 */
@ApplicationScoped
@Slf4j
public class WorkflowProvider {
    @Produces
    public Optional<WorkflowInfo> getWorkflowInfo(@NotNull final ContainerRequestContext context) {
        return Optional.ofNullable((WorkflowInfo) context.getProperty(WorkflowFilter.WORKFLOW_DATA));
    }
}
