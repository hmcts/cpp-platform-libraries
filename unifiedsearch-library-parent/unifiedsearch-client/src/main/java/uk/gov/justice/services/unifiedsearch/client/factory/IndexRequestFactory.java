package uk.gov.justice.services.unifiedsearch.client.factory;

import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIndexerHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import co.elastic.clients.elasticsearch.core.IndexRequest;

@ApplicationScoped
public class IndexRequestFactory {

    @Inject
    private UnifiedSearchIndexerHelper unifiedSearchIndexerHelper;

    public IndexRequest indexRequest(final String indexName,
                                     final Object document,
                                     final String caseId,
                                     final Long sequenceNumber,
                                     final Long primaryTerm) {
        return  IndexRequest.of(i -> i
                .index(indexName)
                .id(caseId)
                .document(document)
                .ifSeqNo(sequenceNumber)
                .ifPrimaryTerm(primaryTerm)
        );
    }
}
