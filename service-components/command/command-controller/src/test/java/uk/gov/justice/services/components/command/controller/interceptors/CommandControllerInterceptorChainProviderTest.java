package uk.gov.justice.services.components.command.controller.interceptors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_CONTROLLER;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
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
public class CommandControllerInterceptorChainProviderTest {

    @Mock
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @InjectMocks
    private CommandControllerInterceptorChainProvider commandControllerInterceptorChainProvider;

    @Test
    public void shouldReturnComponent() throws Exception {
        assertThat(commandControllerInterceptorChainProvider.component(), is(COMMAND_CONTROLLER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultInterceptorChainTypes() throws Exception {

        when(serviceComponentJndiConfig.isCommandControllerAuditEnabled()).thenReturn(true);

        commandControllerInterceptorChainProvider.createInterceptorChainEntries();

        final List<InterceptorChainEntry> interceptorChainTypes = commandControllerInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(5));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(3000, LocalAuditInterceptor.class),
                new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddAuditInterceptorIfNotEnabledInConfiguration() throws Exception {

        when(serviceComponentJndiConfig.isCommandControllerAuditEnabled()).thenReturn(false);

        commandControllerInterceptorChainProvider.createInterceptorChainEntries();

        final List<InterceptorChainEntry> interceptorChainTypes = commandControllerInterceptorChainProvider.interceptorChainTypes();

        assertThat(interceptorChainTypes.size(), is(4));
        assertThat(interceptorChainTypes, containsInAnyOrder(
                new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class),
                new InterceptorChainEntry(5000, FeatureControlInterceptor.class)));
    }
}
