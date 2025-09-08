package uk.gov.justice.services.unifiedsearch.client.index;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

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


