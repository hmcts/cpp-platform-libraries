package uk.gov.justice.services.components.event.indexer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static uk.gov.justice.services.core.annotation.Component.EVENT_INDEXER;

import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.event.source.subscriptions.interceptors.SubscriptionEventInterceptor;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;
import uk.gov.justice.services.unifiedsearch.client.retry.UnifiedSearchIngestionRetryInterceptor;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EventIndexerInterceptorChainProviderTest {

    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(new EventIndexerInterceptorChainProvider().component(), is(EVENT_INDEXER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {
        final List<InterceptorChainEntry> interceptorChainTypes = new EventIndexerInterceptorChainProvider().interceptorChainTypes();

        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(1000, SubscriptionEventInterceptor.class),
                new InterceptorChainEntry(3000, UnifiedSearchIngestionRetryInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
        ));
    }

}