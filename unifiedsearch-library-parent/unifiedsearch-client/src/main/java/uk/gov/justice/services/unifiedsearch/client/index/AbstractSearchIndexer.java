package uk.gov.justice.services.unifiedsearch.client.index;

import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.unifiedsearch.client.validation.JsonDocumentValidator;

import javax.inject.Inject;
import javax.json.JsonObject;

public abstract class AbstractSearchIndexer {

    @Inject
    private JsonDocumentValidator jsonValidator;

    @Inject
    private SearchIndexer searchIndexer;

    public abstract String getIndexSchemaFile();

    public abstract String getIndexName();

    public void indexData(final Envelope<JsonObject> eventWithJoltTransformedPayload) {

        final JsonObject document = eventWithJoltTransformedPayload.payload();
        jsonValidator.validate(document, getIndexSchemaFile());
        searchIndexer.indexData(document, getIndexName());
    }

}
