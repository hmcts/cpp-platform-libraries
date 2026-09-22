package uk.gov.justice.services.unifiedsearch.client.index;


import uk.gov.justice.services.unifiedsearch.client.factory.GetRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.IndexRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.UpdateRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.retry.IngestionResponseVerifier;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.json.JsonObject;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;

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

    protected abstract ElasticsearchClient elasticsearchClient();

    protected abstract Object getTransformedCaseDetails(final JsonObject document,
                                                        final GetResponse getResponse) throws IOException;

    public GetResponse getDocument(final UUID caseId, final String indexName) throws IOException {
        final GetRequest getRequest = getRequestFactory.getRequest(indexName, caseId);
        return elasticsearchClient().get(getRequest, JsonData.class);
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
                caseId.toString(),
                sequenceNumber,
                primaryTerm);

        final IndexResponse indexResponse = elasticsearchClient().index(indexRequest);
        ingestionResponseVerifier.checkCreateSucceeded(caseId, indexResponse);
    }

    public void upsertDocument(final UUID caseId,
                               final JsonObject document, final String indexName) throws IOException {
        final GetResponse getResponse = getDocument(caseId, indexName);
        final Object caseDetails = getTransformedCaseDetails(document, getResponse);

        final IndexRequest indexRequest = indexRequestFactory.indexRequest(
                indexName,
                caseDetails,
                caseId.toString(),
                getResponse.seqNo(),
                getResponse.primaryTerm());

        final UpdateRequest updateRequest = updateRequestFactory.updateRequest(
                indexName,
                caseId.toString(),
                caseDetails,
                indexRequest);



        final UpdateResponse updateResponse = elasticsearchClient().update(updateRequest, Void.class);
        ingestionResponseVerifier.checkUpsertSucceeded(caseId, updateResponse);
    }
}
