package uk.gov.justice.services.components.query.api.interceptors;

import static uk.gov.justice.services.core.annotation.Component.EVENT_API;

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

public class EventApiInterceptorChainProvider implements InterceptorChainEntryProvider {

    private final List<InterceptorChainEntry> interceptorChainEntries = new LinkedList<>();

    @Inject
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @PostConstruct
    public void createInterceptorChainEntries() {
        interceptorChainEntries.add(new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(5000, FeatureControlInterceptor.class));

        if (serviceComponentJndiConfig.isEventApiAuditEnabled()) {
            interceptorChainEntries.add(new InterceptorChainEntry(3000, LocalAuditInterceptor.class));
        }
    }

    @Override
    public String component() {
        return EVENT_API;
    }

    @Override
    public List<InterceptorChainEntry> interceptorChainTypes() {
        return interceptorChainEntries;
    }
}
