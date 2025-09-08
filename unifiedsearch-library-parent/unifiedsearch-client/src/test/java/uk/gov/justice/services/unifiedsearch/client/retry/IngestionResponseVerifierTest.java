package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import java.util.UUID;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IngestionResponseVerifierTest {

    @Mock
    private UpdateResponse updateResponse;

    @Mock
    private IndexResponse indexResponse;

    @InjectMocks
    IngestionResponseVerifier ingestionResponseVerifier;

    @Test
    public void handleUpdateResponseWhenResponseStatusIsOK() {
        final UUID caseId = randomUUID();
        when(updateResponse.status()).thenReturn(RestStatus.OK);
        ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse);
    }

    @Test
    public void handleUpdateResponseWhenResponseStatusIsNotOK() {
        final UUID caseId = randomUUID();
        when(updateResponse.status()).thenReturn(RestStatus.REQUEST_TIMEOUT);

        final UnifiedSearchIngestionException unifiedSearchIngestionException = assertThrows(
                UnifiedSearchIngestionException.class,
                () -> ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse));

        assertThat(unifiedSearchIngestionException.getMessage(), is(format("Ingestion failed for index update for document with id: %s with status REQUEST_TIMEOUT", caseId)));
    }

    @Test
    public void handleIndexResponseWhenResponseStatusIsOK() {
        final UUID caseId = randomUUID();
        when(indexResponse.status()).thenReturn(RestStatus.OK);
        ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse);
    }

    @Test
    public void handleIndexResponseWhenResponseStatusIsNotOK() {
        final UUID caseId = randomUUID();

        when(indexResponse.status()).thenReturn(RestStatus.REQUEST_TIMEOUT);

        final UnifiedSearchIngestionException unifiedSearchIngestionException = assertThrows(
                UnifiedSearchIngestionException.class,
                () -> ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse));

        assertThat(unifiedSearchIngestionException.getMessage(), is(format("Ingestion failed for index write for document with id: %s with status REQUEST_TIMEOUT", caseId)));
    }
}
