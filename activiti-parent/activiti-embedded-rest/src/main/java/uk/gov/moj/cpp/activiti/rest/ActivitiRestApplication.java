package uk.gov.moj.cpp.activiti.rest;

import uk.gov.moj.cpp.activiti.rest.resource.HistoricProcessInstancesResource;
import uk.gov.moj.cpp.activiti.rest.resource.JobsResource;
import uk.gov.moj.cpp.activiti.rest.resource.ProcessInstancesResource;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application that exposes the Activiti management REST API.
 *
 * Replaces the former Spring DispatcherServlet approach (WebConfigurer) which
 * relied on javax.servlet APIs incompatible with Jakarta EE 10. Resources in
 * this application delegate directly to the CDI-injected Activiti engine services.
 *
 * The resources are listed explicitly. An Application that returns no classes gets
 * every root resource and provider in the WAR (Jakarta REST, servlet deployment),
 * which would republish the context's own command, query, event-store and
 * framework endpoints under this path.
 *
 * Deployed at: /{context-root}/internal/activiti/service/
 */
@ApplicationPath("/internal/activiti/service")
public class ActivitiRestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                ProcessInstancesResource.class,
                HistoricProcessInstancesResource.class,
                JobsResource.class);
    }
}
