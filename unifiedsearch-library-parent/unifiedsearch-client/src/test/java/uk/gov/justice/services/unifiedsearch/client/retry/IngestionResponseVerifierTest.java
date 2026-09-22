package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch._types.Result;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import java.util.UUID;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
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
        when(updateResponse.result()).thenReturn(Result.Updated);
        ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse);
    }

    @Test
    public void handleUpdateResponseWhenResponseStatusIsNotOK() {
        final UUID caseId = randomUUID();
        when(updateResponse.result()).thenReturn(Result.NotFound);

        final UnifiedSearchIngestionException unifiedSearchIngestionException = assertThrows(
                UnifiedSearchIngestionException.class,
                () -> ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse));

        assertThat(unifiedSearchIngestionException.getMessage(), is(format("Ingestion failed for index update for document with id: %s with status NotFound", caseId)));
    }

    @Test
    public void handleIndexResponseWhenResponseStatusIsOK() {
        final UUID caseId = randomUUID();
        when(indexResponse.result()).thenReturn(Result.Created);
        ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse);
    }

    @Test
    public void handleIndexResponseWhenResponseStatusIsNotOK() {
        final UUID caseId = randomUUID();

        when(indexResponse.result()).thenReturn(Result.NoOp);

        final UnifiedSearchIngestionException unifiedSearchIngestionException = assertThrows(
                UnifiedSearchIngestionException.class,
                () -> ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse));

        assertThat(unifiedSearchIngestionException.getMessage(), is(format("Ingestion failed for index write for document with id: %s with status NoOp", caseId)));
    }
}
