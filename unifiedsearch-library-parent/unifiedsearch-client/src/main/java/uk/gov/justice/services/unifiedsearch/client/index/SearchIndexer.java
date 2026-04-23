package uk.gov.justice.services.unifiedsearch.client.index;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

@ApplicationScoped
public class SearchIndexer {

    @Inject
    private DocumentIndexer documentIndexer;

    public void indexData(final JsonObject document, final String indexName) {

        final JsonArray caseDocuments = document.getJsonArray("caseDocuments");
        if (caseDocuments != null) {
            for (JsonValue caseDocument : caseDocuments) {
                JsonObject jsonObject = (JsonObject) caseDocument;
                indexData(jsonObject, indexName);
            }
        } else {
            documentIndexer.index(document, indexName);
        }
    }
}


