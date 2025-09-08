package uk.gov.justice.services.components.command.api.interceptors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.QUERY_API;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
import uk.gov.justice.services.components.query.api.interceptors.DefaultQueryApiInterceptorProvider;
import uk.gov.justice.services.components.query.api.interceptors.QueryApiDebugLoggingInterceptor;
import uk.gov.justice.services.components.query.api.interceptors.QueryApiInterceptorChainProvider;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class QueryApiInterceptorChainProviderTest {

    @Mock
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @InjectMocks
    private QueryApiInterceptorChainProvider queryApiInterceptorChainProvider;

    @Mock
    private DefaultQueryApiInterceptorProvider queryApiInterceptor;



    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(queryApiInterceptorChainProvider.component(), is(QUERY_API));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {

        when(serviceComponentJndiConfig.isQueryApiAuditEnabled()).thenReturn(true);

        when(queryApiInterceptor.createAuditInterceptorEntry(3000)).thenReturn(new InterceptorChainEntry(3000, LocalAuditInterceptor.class));

        queryApiInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = queryApiInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(6));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(100, QueryApiDebugLoggingInterceptor.class),
                new InterceptorChainEntry(3000, LocalAuditInterceptor.class),
                new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddAuditInterceptorIfNotEnabledInConfiguration() throws Exception {

        when(serviceComponentJndiConfig.isQueryApiAuditEnabled()).thenReturn(false);

        queryApiInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = queryApiInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(5));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(100, QueryApiDebugLoggingInterceptor.class),
                new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
        ));
    }
}
