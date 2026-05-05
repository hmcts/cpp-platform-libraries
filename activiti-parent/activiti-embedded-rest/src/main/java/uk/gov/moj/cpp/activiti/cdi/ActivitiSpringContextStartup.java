package uk.gov.moj.cpp.activiti.cdi;

import uk.gov.moj.cpp.activiti.spring.conf.ApplicationConfiguration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * CDI startup bean that initialises the Activiti Spring application context.
 *
 * Replaces the former WebConfigurer @WebListener approach, which relied on javax.servlet APIs
 * incompatible with Jakarta EE 10. A plain AnnotationConfigApplicationContext is used so that
 * no servlet layer is required. The Activiti REST management API (/internal/activiti/service/*)
 * is not registered here; add a separate servlet registration if it is needed.
 *
 * Spring scans ProxyProcessEngineLookup (@Component) and calls setApplicationContext() on it,
 * which sets the static HANDLER.processEngine so that CDI injection of RuntimeService works.
 */
@ApplicationScoped
public class ActivitiSpringContextStartup {

    private final Logger log = LoggerFactory.getLogger(ActivitiSpringContextStartup.class);

    void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        log.debug("Initialising Activiti Spring application context");
        try {
            new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            log.debug("Activiti Spring application context fully initialised");
        } catch (final Exception e) {
            log.error("Failed to initialise Activiti Spring application context", e);
            throw new RuntimeException("Failed to initialise Activiti Spring application context", e);
        }
    }
}
