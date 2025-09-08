package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.MetadataBuilder;
import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class UnifiedSearchIngestionRetryInterceptorTest {
    @Mock
    private Logger logger;

    @Mock
    private InterceptorChain interceptorChain;

    @InjectMocks
    private UnifiedSearchIngestionRetryInterceptor retryInterceptor;

    @Test
    public void shouldRetryIfExceptionThrownByDispatcher() throws Exception {
        retryInterceptor.maxRetry = "2";
        retryInterceptor.waitTime = "500";
        final UUID id = randomUUID();
        final MetadataBuilder metadata = metadataOf(id, "nameABC");
        final InterceptorContext currentContext = interceptorContextWithInput(envelope().with(metadata).build());
        final InterceptorContext nextInChain = interceptorContextWithInput(mock(JsonEnvelope.class));

        when(interceptorChain.processNext(currentContext))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 1)))
                .thenReturn(nextInChain);


        assertThat(retryInterceptor.process(currentContext, interceptorChain), is(nextInChain));
        verify(logger, times(1)).debug(format("Unified search ingestion has failed on event  {\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 1"));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 2"));
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldThrowExceptionIfRetryMaxValueIsExceeded() throws Exception {
        retryInterceptor.maxRetry = "2";
        retryInterceptor.waitTime = "500";

        final UUID streamId = randomUUID();
        final UUID id = randomUUID();
        final MetadataBuilder metadata = metadataOf(id, "nameABC");

        final JsonEnvelope envelope = envelope().with(metadata.withStreamId(streamId)).build();
        final InterceptorContext currentContext = interceptorContextWithInput(envelope);

        when(interceptorChain.processNext(currentContext))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 1)))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 2)));

        var e = assertThrows(UnifiedSearchIngestionRetryFailedException.class, () -> {
            retryInterceptor.process(currentContext, interceptorChain);
        });
        assertThat(e.getMessage(), is("Retry count of 2 exceeded for ingestion " + envelope.metadata().asJsonObject()));

        verify(logger, times(1)).debug(format("Unified search ingestion has failed on event  {\"stream\":{\"id\":\"" + streamId + "\"},\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 1"));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 2"));
        verify(logger, times(1)).debug(format("Unified search ingestion has failed on event  {\"stream\":{\"id\":\"" + streamId + "\"},\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 2"));
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldRetryStraightAwayForFourAttemptsThenSucceedonFifthAttempt() throws Exception {
        final UUID id = randomUUID();
        final MetadataBuilder metadata = metadataOf(id, "nameABC");

        final InterceptorContext currentContext = interceptorContextWithInput(envelope().with(metadata).build());
        final InterceptorContext nextInChain = interceptorContextWithInput(mock(JsonEnvelope.class));

        when(interceptorChain.processNext(currentContext))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 1)))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 2)))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 3)))
                .thenThrow(new UnifiedSearchIngestionException(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata.build().toString(), 4)))
                .thenReturn(nextInChain);

        retryInterceptor.maxRetry = "5";
        retryInterceptor.waitTime = "1000";

        final long start = currentTimeMillis();
        assertThat(retryInterceptor.process(currentContext, interceptorChain), is(nextInChain));
        final long end = currentTimeMillis();

        final long runTime = end - start;
        assertThat(runTime > 4000, is(true));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 2"));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 3"));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 4"));
        verify(logger, times(1)).debug(format("Unified search ingestion retry attempt 5"));

        verify(logger,times(1)).debug(format("Unified search ingestion has failed on event  {\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 1"));
        verify(logger,times(1)).debug(format("Unified search ingestion has failed on event  {\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 2"));
        verify(logger,times(1)).debug(format("Unified search ingestion has failed on event  {\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 3"));
        verify(logger,times(1)).debug(format("Unified search ingestion has failed on event  {\"name\":\"nameABC\",\"id\":\"" + id + "\"} at retry attempt 4"));
        verifyNoMoreInteractions(logger);
    }
}
