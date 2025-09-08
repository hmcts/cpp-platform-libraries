package uk.gov.justice.services.components.event.indexer;

import static uk.gov.justice.services.core.annotation.Component.EVENT_INDEXER;

import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntryProvider;
import uk.gov.justice.services.event.source.subscriptions.interceptors.SubscriptionEventInterceptor;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;
import uk.gov.justice.services.unifiedsearch.client.retry.UnifiedSearchIngestionRetryInterceptor;

import java.util.LinkedList;
import java.util.List;


public class EventIndexerInterceptorChainProvider implements InterceptorChainEntryProvider {

    final List<InterceptorChainEntry> interceptorChainEntries = new LinkedList<>();

    public EventIndexerInterceptorChainProvider() {
        interceptorChainEntries.add(new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(1000, SubscriptionEventInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(3000, UnifiedSearchIngestionRetryInterceptor.class));
        interceptorChainEntries.add(new InterceptorChainEntry(5000, FeatureControlInterceptor.class));
    }

    @Override
    public String component() {
        return EVENT_INDEXER;
    }

    @Override
    public List<InterceptorChainEntry> interceptorChainTypes() {
        return interceptorChainEntries;
    }
}
