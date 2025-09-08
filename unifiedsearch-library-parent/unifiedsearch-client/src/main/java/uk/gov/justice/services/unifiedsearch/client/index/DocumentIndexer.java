package uk.gov.justice.services.unifiedsearch.client.index;

import static java.lang.String.format;

import java.io.IOException;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.elasticsearch.ElasticsearchException;

@ApplicationScoped
public class DocumentIndexer {

    @Inject
    private UnifiedSearchIndexerHelper unifiedSearchIndexerHelper;

    @Inject
    private DocumentSelector documentSelector;

    public void index(final JsonObject document, final String indexName) {
        final UUID caseId = unifiedSearchIndexerHelper.getCaseId(document);
        try {
            final DocumentService documentService = documentSelector.getDocumentServiceByIndex(indexName);
            documentService.upsertDocument(caseId, document, indexName);
        } catch (final IOException | ElasticsearchException ioe) {
            throw new UnifiedSearchIngestionException(format("Ingestion failed for index write/update for document with id: %s", caseId), ioe);
        }
    }
}
