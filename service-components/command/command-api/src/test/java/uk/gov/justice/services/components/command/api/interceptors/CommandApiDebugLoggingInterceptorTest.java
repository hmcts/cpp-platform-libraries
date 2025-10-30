package uk.gov.justice.services.components.command.api.interceptors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.DebugLogger;

import java.util.function.Supplier;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

@ExtendWith(MockitoExtension.class)
public class CommandApiDebugLoggingInterceptorTest {

    @Mock
    private Logger logger;

    @Mock
    private DebugLogger debugLogger;

    @InjectMocks
    private CommandApiDebugLoggingInterceptor commandApiDebugLoggingInterceptor;

    @Captor
    private ArgumentCaptor<Supplier<String>> messageCaptor;

    @Test
    public void shouldLogThenContinueToNextInterceptor() {

        final InterceptorContext interceptorContext = mock(InterceptorContext.class);
        final InterceptorChain interceptorChain = mock(InterceptorChain.class);
        final JsonEnvelope jsonEnvelope = envelopeFrom(
                metadataBuilder().withId(randomUUID()).withName("test.command"),
                getJsonBuilderFactory().createObjectBuilder().build());

        when(interceptorChain.processNext(interceptorContext)).thenReturn(interceptorContext);
        when(interceptorContext.inputEnvelope()).thenReturn(jsonEnvelope);

        final InterceptorContext resultInterceptorContext = commandApiDebugLoggingInterceptor.process(interceptorContext, interceptorChain);

        assertThat(resultInterceptorContext, is(interceptorContext));

        verify(debugLogger).debug(eq(logger), messageCaptor.capture());
        assertThat(messageCaptor.getValue().get(), is("Processing Command Name : test.command"));
    }
}