package uk.gov.justice.services.components.command.controller.interceptors;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_CONTROLLER;

import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntryProvider;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class CommandControllerInterceptorChainProvider implements InterceptorChainEntryProvider {

    private final List<InterceptorChainEntry> interceptorChainEntries = new LinkedList<>();

    @Inject
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @PostConstruct
    public void createInterceptorChainEntries() {
        interceptorChainEntries.add(new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(5000, FeatureControlInterceptor.class));

        if (serviceComponentJndiConfig.isCommandControllerAuditEnabled()) {
            interceptorChainEntries.add(new InterceptorChainEntry(3000, LocalAuditInterceptor.class));
        }
    }

    @Override
    public String component() {
        return COMMAND_CONTROLLER;
    }

    @Override
    public List<InterceptorChainEntry> interceptorChainTypes() {
        return interceptorChainEntries;
    }
}
