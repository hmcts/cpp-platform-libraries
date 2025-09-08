package uk.gov.justice.services.unifiedsearch.client.factory;

import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.NONE;
import static org.elasticsearch.xcontent.XContentType.JSON;

import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;

@ApplicationScoped
public class UpdateRequestFactory {

    public UpdateRequest updateRequest(final String indexName,
                                       final String documentId,
                                       final String caseDetailsString,
                                       final IndexRequest indexRequest) {
        return new UpdateRequest(indexName, documentId)
                .doc(caseDetailsString, JSON)
                .setRefreshPolicy(NONE)
                .upsert(indexRequest);
    }

}
