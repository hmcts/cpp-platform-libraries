package uk.gov.justice.services.unifiedsearch.client.index;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.json.JsonArray;
import javax.json.JsonObject;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

@ExtendWith(MockitoExtension.class)
public class SearchIndexerTest {

    @Mock
    private DocumentIndexer documentIndexer;

    @InjectMocks
    private SearchIndexer searchIndexer;

    @Test
    public void shouldExtractEachCaseDocumentFromTheArrayAndIndexEachOne() throws Exception {

        final JsonObject outerDocument = mock(JsonObject.class);
        final JsonObject caseDocument_1 = mock(JsonObject.class);
        final JsonObject caseDocument_2 = mock(JsonObject.class);
        final JsonObject caseDocument_3 = mock(JsonObject.class);
        final String indexName = "testIndex";

        final JsonArray caseDocuments = getJsonBuilderFactory().createArrayBuilder()
                .add(caseDocument_1)
                .add(caseDocument_2)
                .add(caseDocument_3)
                .build();

        when(outerDocument.getJsonArray("caseDocuments")).thenReturn(caseDocuments);

        searchIndexer.indexData(outerDocument, indexName);

        final InOrder inOrder = inOrder(documentIndexer);

        inOrder.verify(documentIndexer).index(caseDocument_1, indexName);
        inOrder.verify(documentIndexer).index(caseDocument_2, indexName);
        inOrder.verify(documentIndexer).index(caseDocument_3, indexName);

        verify(documentIndexer, never()).index(outerDocument, indexName);
    }
}
