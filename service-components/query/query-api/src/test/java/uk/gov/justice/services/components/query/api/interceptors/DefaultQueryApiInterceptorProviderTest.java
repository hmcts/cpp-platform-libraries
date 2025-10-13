package uk.gov.justice.services.components.query.api.interceptors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import uk.gov.justice.services.components.configuration.ServiceComponentJndiConfig;
import uk.gov.justice.services.core.accesscontrol.LocalAccessControlInterceptor;
import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.featurecontrol.FeatureControlInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.DebugLogger;
import uk.gov.justice.services.metrics.interceptor.IndividualActionMetricsInterceptor;
import uk.gov.justice.services.metrics.interceptor.TotalActionMetricsInterceptor;

import java.util.List;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.QUERY_API;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;

public class DefaultQueryApiInterceptorProviderTest {

    @ExtendWith(MockitoExtension.class)
    public static class QueryApiDebugLoggingInterceptorTest {

        @Mock
        private Logger logger;

        @Mock
        private DebugLogger debugLogger;

        @InjectMocks
        private QueryApiDebugLoggingInterceptor queryApiDebugLoggingInterceptor1;

        @Captor
        private ArgumentCaptor<Supplier<String>> messageCaptor;

        @Mock
        private DefaultQueryApiInterceptorProvider queryApiInterceptor;

        @Test
        public void shouldLogThenContinueToNextInterceptor() {

            final InterceptorContext interceptorContext = mock(InterceptorContext.class);
            final InterceptorChain interceptorChain = mock(InterceptorChain.class);
            final JsonEnvelope jsonEnvelope = envelopeFrom(
                    metadataBuilder().withId(randomUUID()).withName("test.query"),
                    jsonBuilderFactory.createObjectBuilder().build());

            when(interceptorChain.processNext(interceptorContext)).thenReturn(interceptorContext);
            when(interceptorContext.inputEnvelope()).thenReturn(jsonEnvelope);


            final InterceptorContext resultInterceptorContext = queryApiDebugLoggingInterceptor1.process(interceptorContext, interceptorChain);

            assertThat(resultInterceptorContext, is(interceptorContext));

            verify(debugLogger).debug(eq(logger), messageCaptor.capture());
            assertThat(messageCaptor.getValue().get(), is("Processing Query Name : test.query"));
        }
    }

    @ExtendWith(MockitoExtension.class)
    public static class QueryApiInterceptorChainProviderTest {

        @Mock
        private ServiceComponentJndiConfig serviceComponentJndiConfig;

        @InjectMocks
        private QueryApiInterceptorChainProvider queryApiInterceptorChainProvider;

        @Mock
        private DefaultQueryApiInterceptorProvider queryApiInterceptor;



        @Test
        public void shouldReturnComponent() throws Exception {
            assertThat(queryApiInterceptorChainProvider.component(), Matchers.is(QUERY_API));
        }

        @Test
        public void shouldProvideDefaultInterceptorChainTypes() throws Exception {

            when(serviceComponentJndiConfig.isQueryApiAuditEnabled()).thenReturn(true);

            when(queryApiInterceptor.createAuditInterceptorEntry(3000)).thenReturn(new InterceptorChainEntry(3000, LocalAuditInterceptor.class));

            queryApiInterceptorChainProvider.createInterceptorChainEntries();
            final List<InterceptorChainEntry> interceptorChainTypes = queryApiInterceptorChainProvider.interceptorChainTypes();

            assertThat(interceptorChainTypes.size(), Matchers.is(6));
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
        public void shouldNotAddAuditInterceptorIfNotEnabledInConfiguration() throws Exception {

            when(serviceComponentJndiConfig.isQueryApiAuditEnabled()).thenReturn(false);

            queryApiInterceptorChainProvider.createInterceptorChainEntries();
            final List<InterceptorChainEntry> interceptorChainTypes = queryApiInterceptorChainProvider.interceptorChainTypes();

            assertThat(interceptorChainTypes.size(), Matchers.is(5));
            assertThat(interceptorChainTypes, containsInAnyOrder(
                    new InterceptorChainEntry(1, TotalActionMetricsInterceptor.class),
                    new InterceptorChainEntry(2, IndividualActionMetricsInterceptor.class),
                    new InterceptorChainEntry(100, QueryApiDebugLoggingInterceptor.class),
                    new InterceptorChainEntry(4000, LocalAccessControlInterceptor.class),
                    new InterceptorChainEntry(5000, FeatureControlInterceptor.class)
            ));
        }
    }
}