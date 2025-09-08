package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.String.format;
import static org.elasticsearch.rest.RestStatus.CREATED;
import static org.elasticsearch.rest.RestStatus.OK;

import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.rest.RestStatus;

@ApplicationScoped
public class IngestionResponseVerifier {

    public void checkUpsertSucceeded(final UUID caseId, final UpdateResponse updateResponse) {
        final RestStatus status = updateResponse.status();
        if (status != OK && status != CREATED) {
            throw new UnifiedSearchIngestionException(format("Ingestion failed for index update for document with id: %s with status %s", caseId, updateResponse.status()));
        }
    }

    public void checkCreateSucceeded(final UUID caseId, final IndexResponse indexResponse) {
        if (indexResponse.status() != OK && indexResponse.status() != CREATED) {
            throw new UnifiedSearchIngestionException(format("Ingestion failed for index write for document with id: %s with status %s", caseId, indexResponse.status()));
        }
    }
}
