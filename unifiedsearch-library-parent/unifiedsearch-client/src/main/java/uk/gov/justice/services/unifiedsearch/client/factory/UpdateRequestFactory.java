package uk.gov.justice.services.unifiedsearch.client.factory;


import javax.enterprise.context.ApplicationScoped;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;

@ApplicationScoped
public class UpdateRequestFactory {


    public UpdateRequest updateRequest(final String indexName,
                                       final String documentId,
                                       final Object document,
                                       final IndexRequest indexRequest) {
        return UpdateRequest.of(u -> u
                .index(indexName)
                .id(documentId)
                .doc(document)
                .upsert(indexRequest)
        );
    }

}
