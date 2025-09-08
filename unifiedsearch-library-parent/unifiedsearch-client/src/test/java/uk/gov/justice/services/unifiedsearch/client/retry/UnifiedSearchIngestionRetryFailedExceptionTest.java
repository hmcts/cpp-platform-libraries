package uk.gov.justice.services.unifiedsearch.client.retry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.jupiter.api.Test;

public class UnifiedSearchIngestionRetryFailedExceptionTest {
    @Test
    public void shouldCreateInstanceOfUnifiedSearchIngestionRetryFailedExceptionWithMessage() throws Exception {
        final UnifiedSearchIngestionRetryFailedException exception = new UnifiedSearchIngestionRetryFailedException("Test message" , new RuntimeException());
        assertThat(exception.getMessage(), is("Test message"));
        assertThat(exception, instanceOf(Exception.class));
    }
}