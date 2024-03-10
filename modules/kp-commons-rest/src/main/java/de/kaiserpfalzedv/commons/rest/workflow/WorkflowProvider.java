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

package de.kaiserpfalzedv.commons.rest.workflow;

import java.util.Optional;

import de.kaiserpfalzedv.commons.core.workflow.WorkflowInfoImpl;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

/**
 * WorkflowProvider -- Provides the workflow data of the current request.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-04
 */
@Singleton
public class WorkflowProvider {
    private final ThreadLocal<WorkflowInfoImpl> infos = new WorkflowInfoThreadLocal();

    @Produces
    public Optional<WorkflowInfoImpl> getWorkflowInfo() {
        return Optional.ofNullable(this.infos.get());
    }

    public void registerWorkflowInfo(@NotNull final WorkflowInfoImpl context) {
        this.infos.set(context);
    }

    public void unregisterWorkflowInfo() {
        this.infos.remove();
    }

    /**
     * Subclass of {@link ThreadLocal} to generate an initial {@link WorkflowInfoImpl}.
     */
    private static class WorkflowInfoThreadLocal extends ThreadLocal<WorkflowInfoImpl> {
        @Override
        public WorkflowInfoImpl initialValue() {
            return WorkflowInfoImpl.builder().build();
        }
    }
}
