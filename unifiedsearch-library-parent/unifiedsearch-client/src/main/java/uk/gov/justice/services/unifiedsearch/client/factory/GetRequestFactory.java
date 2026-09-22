package uk.gov.justice.services.unifiedsearch.client.factory;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import co.elastic.clients.elasticsearch.core.GetRequest;

@ApplicationScoped
public class GetRequestFactory {

    public GetRequest getRequest(final String indexName, final UUID documentId) {
        return GetRequest.of(r -> r
                .index(indexName)
                .id(documentId.toString()));
    }
}
