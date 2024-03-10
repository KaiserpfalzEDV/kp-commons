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

package de.kaiserpfalzedv.commons.rest.workflow;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

import org.slf4j.MDC;

import de.kaiserpfalzedv.commons.core.workflow.WorkflowDetailInfoImpl;
import de.kaiserpfalzedv.commons.core.workflow.WorkflowInfoImpl;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>WorkflowFilter -- Handles the WorkflowInfo for REST requests.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-04
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
@Slf4j
public class WorkflowFilter implements Filter {
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
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = this.checkForHttpServletRequestOrThrowException(request);
        final HttpServletResponse res = this.checkForHttpServletResponseOrThrowException(response);

        this.filter(req);
        chain.doFilter(request, response);
        this.filter(req, res);
    }

    private HttpServletRequest checkForHttpServletRequestOrThrowException(final ServletRequest request) throws ServletException {
        if (! (request instanceof HttpServletRequest)) {
            throw new ServletException("Wrong servlet type. This filter only works on HTTP servlet.");
        }

        return (HttpServletRequest) request;
    }

    private HttpServletResponse checkForHttpServletResponseOrThrowException(final ServletResponse response) throws ServletException {
        if (! (response instanceof HttpServletResponse)) {
            throw new ServletException("Wrong servlet type. This filter only works on HTTP servlet.");
        }

        return (HttpServletResponse) response;
    }

    private void filter(final HttpServletRequest context) {
        final WorkflowInfoImpl info = this.getWorkflowInfo(context);
        this.prepareMDC(info);

        context.setAttribute(WORKFLOW_DATA, info);
        this.provider.registerWorkflowInfo(info);

        log.trace(
                "Created the workflow info. workflow='{}', action='{}', call='{}', user='{}'",
                info.getWorkflow().getId(),
                info.getAction().getId(),
                info.getCall().getId(),
                info.getUser()
        );
    }

    private WorkflowInfoImpl getWorkflowInfo(final HttpServletRequest context) {
        return WorkflowInfoImpl.builder()
                .user(this.checkValidHeader(context.getHeader(WORKFLOW_USER)))
                .workflow(this.getWorkflowInfoDetail(context, WORKFLOW_PREFIX))
                .action(this.getWorkflowInfoDetail(context, ACTION_PREFIX))
                .call(this.getWorkflowInfoDetail(context, CALL_PREFIX))
                .build();
    }

    private WorkflowDetailInfoImpl getWorkflowInfoDetail(final HttpServletRequest context, final String prefix) {
        final String name = this.checkValidHeader(context.getHeader(prefix + NAME));
        final String id = this.checkValidHeader(context.getHeader(prefix + ID));
        final String response = this.checkValidHeader(context.getHeader(prefix + RESPONSE));

        final WorkflowDetailInfoImpl.WorkflowDetailInfoImplBuilder result = WorkflowDetailInfoImpl.builder()
                .created(this.checkValidTimeHeader(context.getHeader(prefix + CREATED), Duration.ofMillis(0)))
                .ttl(this.checkValidTimeHeader(context.getHeader(prefix + TTL), Duration.of(10, ChronoUnit.YEARS)));

        if (name != null) result.name(name);
        if (id != null) result.id(id);
        if (response != null) result.responseChannel(response);

        return result.build();
    }

    private String checkValidHeader(final String value) {
        if (value == null || "".equals(value)) {
            return null;
        }

        return value;
    }

    private OffsetDateTime checkValidTimeHeader(final String value, final TemporalAmount futureOffset) {
        try {
            return OffsetDateTime.parse(value);
        } catch (final DateTimeParseException e) {
            return OffsetDateTime.now(ZoneId.of("UTC")).plus(futureOffset);
        }
    }


    private void prepareMDC(final WorkflowInfoImpl info) {
        MDC.put(WORKFLOW_USER, info.getUser());

        this.putMDC(info.getWorkflow(), WORKFLOW_PREFIX);
        this.putMDC(info.getAction(), ACTION_PREFIX);
        this.putMDC(info.getCall(), CALL_PREFIX);
    }

    private void putMDC(final WorkflowDetailInfoImpl info, final String workflowPrefix) {
        MDC.put(workflowPrefix + NAME, info.getName());
        MDC.put(workflowPrefix + ID, info.getId());
        MDC.put(workflowPrefix + CREATED, info.getCreated().toString());
        MDC.put(workflowPrefix + TTL, info.getTtl().toString());
        MDC.put(workflowPrefix + RESPONSE, info.getResponseChannel());
    }


    private void filter(final HttpServletRequest request, final HttpServletResponse context) {
        this.provider.getWorkflowInfo().ifPresentOrElse(
            info -> {
                log.trace(
                    "Removing workflow data from request. workflow='{}', action='{}', call='{}', user='{}'",
                    info.getWorkflow().getId(),
                    info.getAction().getId(),
                    info.getCall().getId(),
                    info.getUser()
                );
        
                this.unsetWorkflowInfoInContext(request);
                this.provider.unregisterWorkflowInfo();
        
                this.removeMDC();
            }, 
            () -> {
                log.trace("No workflow data to remove from request.");
            }
        );
    }

    private void unsetWorkflowInfoInContext(final HttpServletRequest requestContext) {
        requestContext.setAttribute(WORKFLOW_DATA, null);
    }

    private void removeMDC() {
        MDC.remove(WORKFLOW_USER);
        this.removeMDC(WORKFLOW_PREFIX);
        this.removeMDC(ACTION_PREFIX);
        this.removeMDC(CALL_PREFIX);
    }

    private void removeMDC(final String prefix) {
        MDC.remove(prefix + NAME);
        MDC.remove(prefix + ID);
        MDC.remove(prefix + CREATED);
        MDC.remove(prefix + TTL);
        MDC.remove(prefix + RESPONSE);
    }
}
