package uk.gov.moj.cpp.activiti.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS application that exposes the Activiti management REST API.
 *
 * Replaces the former Spring DispatcherServlet approach (WebConfigurer) which
 * relied on javax.servlet APIs incompatible with Jakarta EE 10. Resources in
 * this application delegate directly to the CDI-injected Activiti engine services.
 *
 * Deployed at: /{context-root}/internal/activiti/service/
 */
@ApplicationPath("/internal/activiti/service")
public class ActivitiRestApplication extends Application {
}
