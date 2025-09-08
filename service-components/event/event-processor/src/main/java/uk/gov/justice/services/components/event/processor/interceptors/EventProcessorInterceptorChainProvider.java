package uk.gov.justice.services.components.event.processor.interceptors;

import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;

import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntryProvider;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.LinkedList;
import java.util.List;

public class EventProcessorInterceptorChainProvider implements InterceptorChainEntryProvider {

    final List<InterceptorChainEntry> interceptorChainEntries = new LinkedList<>();

    public EventProcessorInterceptorChainProvider() {
        interceptorChainEntries.add(new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(5000, FeatureControlInterceptor.class));
    }

    @Override
    public String component() {
        return EVENT_PROCESSOR;
    }

    @Override
    public List<InterceptorChainEntry> interceptorChainTypes() {
        return interceptorChainEntries;
    }
}
