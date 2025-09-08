package uk.gov.justice.services.components.command.handler.interceptors;

import static java.lang.Integer.MAX_VALUE;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;

import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
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

public class CommandHandlerInterceptorChainProvider implements InterceptorChainEntryProvider {

    private final List<InterceptorChainEntry> interceptorChainEntries = new LinkedList<>();

    @Inject
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @PostConstruct
    public void createInterceptorChainEntries() {
        interceptorChainEntries.add(new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(5000, FeatureControlInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(MAX_VALUE, RetryInterceptor.class));

        if (serviceComponentJndiConfig.isCommandHandlerAuditEnabled()) {
            interceptorChainEntries.add(new InterceptorChainEntry(3000, LocalAuditInterceptor.class));
        }
    }

    @Override
    public String component() {
        return COMMAND_HANDLER;
    }

    @Override
    public List<InterceptorChainEntry> interceptorChainTypes() {
        return interceptorChainEntries;
    }
}
