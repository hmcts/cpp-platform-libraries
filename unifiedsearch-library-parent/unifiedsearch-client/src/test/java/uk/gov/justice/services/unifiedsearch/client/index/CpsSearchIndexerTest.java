package uk.gov.justice.services.unifiedsearch.client.index;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;
import uk.gov.justice.services.unifiedsearch.client.validation.JsonDocumentValidator;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CpsSearchIndexerTest {

    @Mock
    private JsonDocumentValidator jsonValidator;

    @Mock
    private SearchIndexer searchIndexer;

    @InjectMocks
    private CpsSearchIndexer cpsSearchIndexer;

    @Test
    public void shouldUnwrapTheDocumentAndPassToTheSearchIndexer() throws Exception {

        final Envelope<JsonObject> eventWithJoltTransformedPayload = mock(Envelope.class);
        final JsonObject document = mock(JsonObject.class);

        when(eventWithJoltTransformedPayload.payload()).thenReturn(document);

        cpsSearchIndexer.indexData(eventWithJoltTransformedPayload);

        final InOrder inOrder = inOrder(jsonValidator, searchIndexer);

        inOrder.verify(jsonValidator).validate(document, IndexInfo.CPS_CASE.getIndexSchemaFile());
        inOrder.verify(searchIndexer).indexData(document, IndexInfo.CPS_CASE.getIndexName());
    }
}
