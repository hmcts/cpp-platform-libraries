package uk.gov.justice.services.unifiedsearch.client.index;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

import uk.gov.justice.services.unifiedsearch.client.factory.GetRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.IndexRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.UpdateRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.retry.IngestionResponseVerifier;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;

public abstract class DocumentService {

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    private IndexRequestFactory indexRequestFactory;

    @Inject
    private UpdateRequestFactory updateRequestFactory;

    @Inject
    private GetRequestFactory getRequestFactory;

    @Inject
    private IngestionResponseVerifier ingestionResponseVerifier;

    protected abstract RestHighLevelClient restHighLevelClient();

    protected abstract Object getTransformedCaseDetails(final JsonObject document,
                                                        final GetResponse getResponse) throws IOException;

    public GetResponse getDocument(final UUID caseId, final String indexName) throws IOException {
        final GetRequest getRequest = getRequestFactory.getRequest(indexName, caseId);
        return restHighLevelClient().get(getRequest, DEFAULT);
    }

    public void createDocument(
            final JsonObject document,
            final UUID caseId,
            final long sequenceNumber,
            final long primaryTerm,
            final String indexName) throws IOException {

        final IndexRequest indexRequest = indexRequestFactory.indexRequest(
                indexName,
                document,
                sequenceNumber,
                primaryTerm);

        final IndexResponse indexResponse = restHighLevelClient().index(indexRequest, DEFAULT);
        ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse);
    }

    public void upsertDocument(final UUID caseId,
                               final JsonObject document, final String indexName) throws IOException {
        final GetResponse getResponse = getDocument(caseId, indexName);

        final String caseDetailsString = objectMapper.writeValueAsString(getTransformedCaseDetails(document, getResponse));

        final IndexRequest indexRequest = indexRequestFactory.indexRequest(
                indexName,
                document,
                getResponse.getSeqNo(),
                getResponse.getPrimaryTerm());

        final UpdateRequest updateRequest = updateRequestFactory.updateRequest(
                indexName,
                caseId.toString(),
                caseDetailsString,
                indexRequest);

        final UpdateResponse updateResponse = restHighLevelClient().update(updateRequest, DEFAULT);
        ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse);
    }
}
