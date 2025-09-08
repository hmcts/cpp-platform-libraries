package uk.gov.justice.services.unifiedsearch.client.factory;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.get.GetRequest;

@ApplicationScoped
public class GetRequestFactory {

    public GetRequest getRequest(final String indexName, final UUID documentId) {
        return new GetRequest(indexName).id(documentId.toString());
    }
}
