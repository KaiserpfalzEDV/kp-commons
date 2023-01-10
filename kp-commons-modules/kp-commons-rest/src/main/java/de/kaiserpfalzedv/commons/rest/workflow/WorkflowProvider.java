/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.rest.workflow;

import de.kaiserpfalzedv.commons.core.workflow.WorkflowInfo;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
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
    private final ThreadLocal<WorkflowInfo> infos = new WorkflowInfoThreadLocal();

    @Produces
    public Optional<WorkflowInfo> getWorkflowInfo() {
        return Optional.ofNullable(infos.get());
    }

    public void registerWorkflowInfo(@NotNull final WorkflowInfo context) {
        infos.set(context);
    }

    public void unregisterWorkflowInfo() {
        infos.remove();
    }

    /**
     * Subclass of {@link ThreadLocal} to generate an initial {@link WorkflowInfo}.
     */
    private class WorkflowInfoThreadLocal extends ThreadLocal<WorkflowInfo> {
        @Override
        public WorkflowInfo initialValue() {
            return WorkflowInfo.builder().build();
        }
    }
}
