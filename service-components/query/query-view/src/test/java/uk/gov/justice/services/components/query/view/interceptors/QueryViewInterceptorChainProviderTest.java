package uk.gov.justice.services.components.query.view.interceptors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.QUERY_VIEW;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryViewInterceptorChainProviderTest {

    @Mock
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @InjectMocks
    private QueryViewInterceptorChainProvider queryViewInterceptorChainProvider;


    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(queryViewInterceptorChainProvider.component(), is(QUERY_VIEW));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {

        when(serviceComponentJndiConfig.isQueryViewAuditEnabled()).thenReturn(true);

        queryViewInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = queryViewInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(4));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(3000, LocalAuditInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddAuditInterceptorIfNotEnabledInConfiguration() throws Exception {

        when(serviceComponentJndiConfig.isQueryViewAuditEnabled()).thenReturn(false);

        queryViewInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = queryViewInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(3));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
        ));
    }
}
