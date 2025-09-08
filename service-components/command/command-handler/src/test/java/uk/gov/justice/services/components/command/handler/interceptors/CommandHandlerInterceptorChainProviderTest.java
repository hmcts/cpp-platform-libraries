package uk.gov.justice.services.components.command.handler.interceptors;

import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;

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
public class CommandHandlerInterceptorChainProviderTest {

    @Mock
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @InjectMocks
    private CommandHandlerInterceptorChainProvider commandHandlerInterceptorChainProvider;

    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(commandHandlerInterceptorChainProvider.component(), is(COMMAND_HANDLER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {

        when(serviceComponentJndiConfig.isCommandHandlerAuditEnabled()).thenReturn(true);

        commandHandlerInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = commandHandlerInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(5));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(3000, LocalAuditInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class),
                new InterceptorChainEntry(MAX_VALUE, RetryInterceptor.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddAuditInterceptorIfNotEnabledInConfiguration() throws Exception {

        when(serviceComponentJndiConfig.isCommandHandlerAuditEnabled()).thenReturn(false);

        commandHandlerInterceptorChainProvider.createInterceptorChainEntries();
        final List<InterceptorChainEntry> interceptorChainTypes = commandHandlerInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(4));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class),
                new InterceptorChainEntry(MAX_VALUE, RetryInterceptor.class)));
    }
}
