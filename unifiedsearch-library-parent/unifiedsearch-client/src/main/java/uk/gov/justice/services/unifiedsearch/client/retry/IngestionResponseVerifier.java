package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.String.format;

import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch._types.Result;

@ApplicationScoped
public class IngestionResponseVerifier {

    public void checkUpsertSucceeded(final UUID caseId, final UpdateResponse updateResponse) {
        final Result status = updateResponse.result();
        if (status != Result.Updated && status != Result.Created && status != Result.NoOp)  {
            throw new UnifiedSearchIngestionException(format("Ingestion failed for index update for document with id: %s with status %s", caseId, status));
        }
    }

    public void checkCreateSucceeded(final UUID caseId, final IndexResponse indexResponse) {
        if (indexResponse.result() != Result.Updated && indexResponse.result() != Result.Created) {
            throw new UnifiedSearchIngestionException(format("Ingestion failed for index write for document with id: %s with status %s", caseId, indexResponse.result()));
        }
    }
}
