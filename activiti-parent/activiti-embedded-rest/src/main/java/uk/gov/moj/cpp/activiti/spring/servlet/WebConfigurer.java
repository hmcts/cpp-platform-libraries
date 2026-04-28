package uk.gov.moj.cpp.activiti.spring.servlet;

/**
 * Formerly a @WebListener that bootstrapped the Activiti Spring context via the servlet container.
 *
 * Replaced by ActivitiSpringContextStartup, a CDI @ApplicationScoped startup observer, because
 * Spring 4.x uses javax.servlet APIs which are incompatible with Jakarta EE 10 / WildFly 32.
 * This class is retained as an empty stub to avoid ClassNotFoundException in any existing
 * references, but it performs no work.
 */
public class WebConfigurer {
}
