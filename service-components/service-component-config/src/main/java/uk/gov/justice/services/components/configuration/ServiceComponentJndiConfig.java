package uk.gov.justice.services.components.configuration;

import static java.lang.Boolean.parseBoolean;

import uk.gov.justice.services.common.configuration.Value;

import javax.inject.Inject;

public class ServiceComponentJndiConfig {

    @Inject
    @Value(key = "configuration.jndi.command-api.audit.enabled", defaultValue = "true")
    private String commandApiAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.command-controller.audit.enabled", defaultValue = "false")
    private String commandControllerAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.command-handler.audit.enabled", defaultValue = "false")
    private String commandHandlerAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.event-api.audit.enabled", defaultValue = "true")
    private String eventApiAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.query-api.audit.enabled", defaultValue = "true")
    private String queryApiAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.query-controller.audit.enabled", defaultValue = "false")
    private String queryControllerAuditEnabled;

    @Inject
    @Value(key = "configuration.jndi.query-view.audit.enabled", defaultValue = "false")
    private String queryViewAuditEnabled;

    public boolean isCommandApiAuditEnabled() {
        return parseBoolean(commandApiAuditEnabled);
    }

    public boolean isCommandControllerAuditEnabled() {
        return parseBoolean(commandControllerAuditEnabled);
    }

    public boolean isCommandHandlerAuditEnabled() {
        return parseBoolean(commandHandlerAuditEnabled);
    }

    public boolean isEventApiAuditEnabled() {
        return parseBoolean(eventApiAuditEnabled);
    }

    public boolean isQueryApiAuditEnabled() {
        return parseBoolean(queryApiAuditEnabled);
    }

    public boolean isQueryControllerAuditEnabled() {
        return parseBoolean(queryControllerAuditEnabled);
    }

    public boolean isQueryViewAuditEnabled() {
        return parseBoolean(queryViewAuditEnabled);
    }
}
