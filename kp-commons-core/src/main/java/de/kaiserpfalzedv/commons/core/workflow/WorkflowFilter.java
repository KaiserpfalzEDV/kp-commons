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

package de.kaiserpfalzedv.commons.core.workflow;

import de.kaiserpfalzedv.commons.core.workflow.rest.WorkflowProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

/**
 * WorkflowFilter -- Handles the WorkflowInfo for REST requests.
 *
 *
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-04
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Produces
@ApplicationScoped
@Slf4j
public class WorkflowFilter implements ContainerRequestFilter, ClientResponseFilter {
    public static final String WORKFLOW_DATA = "de.kaiserpfalzedv.commons.core.workflow.data";

    private static final String WORKFLOW_USER = "X-wf-user";

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String CREATED = "created";
    private static final String TTL = "ttl";
    private static final String RESPONSE = "response";

    private static final String WORKFLOW_PREFIX = "X-wf-";
    private static final String ACTION_PREFIX = "X-wf-action-";
    private static final String CALL_PREFIX = "X-wf-call-";

    private final WorkflowProvider provider;

    @Override
    public void filter(ContainerRequestContext context) {
        WorkflowInfo info = getWorkflowInfo(context);
        prepareMDC(info);

        context.setProperty(WORKFLOW_DATA, info);
        provider.registerWorkflowInfo(info);

        log.trace(
                "Created the workflow info. workflow='{}', action='{}', call='{}', user='{}'",
                info.getWorkflow().getId(),
                info.getAction().getId(),
                info.getCall().getId(),
                info.getUser()
        );
    }

    private WorkflowInfo getWorkflowInfo(final ContainerRequestContext context) {
        return WorkflowInfo.builder()
                .withUser(checkValidHeader(context.getHeaderString(WORKFLOW_USER)))
                .withWorkflow(getWorkflowInfoDetail(context, WORKFLOW_PREFIX))
                .withAction(getWorkflowInfoDetail(context, ACTION_PREFIX))
                .withCall(getWorkflowInfoDetail(context, CALL_PREFIX))
                .build();
    }

    private WorkflowDetailInfo getWorkflowInfoDetail(final ContainerRequestContext context, final String prefix) {
        String name = checkValidHeader(context.getHeaderString(prefix + NAME));
        String id = checkValidHeader(context.getHeaderString(prefix + ID));
        String response = checkValidHeader(context.getHeaderString(prefix + RESPONSE));

        WorkflowDetailInfo.WorkflowDetailInfoBuilder result = WorkflowDetailInfo.builder()
                .withCreated(checkValidTimeHeader(context.getHeaderString(prefix + CREATED), Duration.ofMillis(0)))
                .withTtl(checkValidTimeHeader(context.getHeaderString(prefix + TTL), Duration.of(10, ChronoUnit.YEARS)));

        if (name != null) result.withName(name);
        if (id != null) result.withId(id);
        if (response != null) result.withResponseChannel(response);

        return result.build();
    }

    private String checkValidHeader(final String value) {
        if (value == null || "".equals(value)) {
            return null;
        }

        return value;
    }

    private OffsetDateTime checkValidTimeHeader(final String value, TemporalAmount futureOffset) {
        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return OffsetDateTime.now(ZoneId.of("UTC")).plus(futureOffset);
        }
    }


    private void prepareMDC(final WorkflowInfo info) {
        MDC.put(WORKFLOW_USER, info.getUser());

        putMDC(info.getWorkflow(), WORKFLOW_PREFIX);
        putMDC(info.getAction(), ACTION_PREFIX);
        putMDC(info.getCall(), CALL_PREFIX);
    }

    private void putMDC(WorkflowDetailInfo info, String workflowPrefix) {
        MDC.put(workflowPrefix + NAME, info.getName());
        MDC.put(workflowPrefix + ID, info.getId());
        MDC.put(workflowPrefix + CREATED, info.getCreated().toString());
        MDC.put(workflowPrefix + TTL, info.getTtl().toString());
        MDC.put(workflowPrefix + RESPONSE, info.getResponseChannel());
    }


    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        WorkflowInfo info = getWorkflowInfoFromContext(requestContext);

        if (info == null) {
            log.trace("There is no workflow data on this request.");
            return;
        }

        log.trace(
                "Removing workflow data from request. workflow='{}', action='{}', call='{}', user='{}'",
                info.getWorkflow().getId(),
                info.getAction().getId(),
                info.getCall().getId(),
                info.getUser()
        );

        unsetWorkflowInfoInContext(requestContext);
        provider.unregisterWorkflowInfo();

        removeMDC();
    }

    private WorkflowInfo getWorkflowInfoFromContext(ClientRequestContext requestContext) {
        return (WorkflowInfo) requestContext.getProperty(WORKFLOW_DATA);
    }

    private void unsetWorkflowInfoInContext(ClientRequestContext requestContext) {
        requestContext.setProperty(WORKFLOW_DATA, null);
    }

    private void removeMDC() {
        MDC.remove(WORKFLOW_USER);
        removeMDC(WORKFLOW_PREFIX);
        removeMDC(ACTION_PREFIX);
        removeMDC(CALL_PREFIX);
    }

    private void removeMDC(final String prefix) {
        MDC.remove(prefix + NAME);
        MDC.remove(prefix + ID);
        MDC.remove(prefix + CREATED);
        MDC.remove(prefix + TTL);
        MDC.remove(prefix + RESPONSE);
    }
}
