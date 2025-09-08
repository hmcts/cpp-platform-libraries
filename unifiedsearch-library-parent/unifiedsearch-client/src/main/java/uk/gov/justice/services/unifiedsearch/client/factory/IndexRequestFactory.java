package uk.gov.justice.services.unifiedsearch.client.factory;

import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.NONE;
import static org.elasticsearch.xcontent.XContentType.JSON;

import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIndexerHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.elasticsearch.action.index.IndexRequest;

@ApplicationScoped
public class IndexRequestFactory {

    @Inject
    private UnifiedSearchIndexerHelper unifiedSearchIndexerHelper;

    public IndexRequest indexRequest(final String indexName,
                                     final JsonObject document,
                                     final long sequenceNumber,
                                     final long primaryTerm) {
        return new IndexRequest(indexName)
                .source(document.toString(), JSON)
                .id(unifiedSearchIndexerHelper.getCaseId(document).toString())
                .setRefreshPolicy(NONE)
                .setIfSeqNo(sequenceNumber)
                .setIfPrimaryTerm(primaryTerm);
    }
}
