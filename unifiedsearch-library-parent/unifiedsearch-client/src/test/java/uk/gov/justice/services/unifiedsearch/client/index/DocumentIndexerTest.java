package uk.gov.justice.services.unifiedsearch.client.index;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class DocumentIndexerTest {

    @Mock
    private UnifiedSearchIndexerHelper unifiedSearchIndexerHelper;

    @Mock
    private DocumentSelector documentSelector;

    @Mock
    private CpsCaseDocumentService cpsCaseDocumentService;

    @Mock
    private CrimeCaseDocumentService crimeCaseDocumentService;

    @Mock
    private IndexTypeLiteral indexTypeLiteral;

    @InjectMocks
    private DocumentIndexer documentIndexer;

    @Test
    public void shouldUpsertDocumentIfDocumentComes() throws Exception {
        final UUID caseId = randomUUID();
        final JsonObject document = mock(JsonObject.class);
        final String indexName = "textIndex";

        when(unifiedSearchIndexerHelper.getCaseId(document)).thenReturn(caseId);
        when(documentSelector.getDocumentServiceByIndex(any())).thenReturn(cpsCaseDocumentService);
        doNothing().when(cpsCaseDocumentService).upsertDocument(caseId, document, indexName);

        documentIndexer.index(document, indexName);

        verify(cpsCaseDocumentService).upsertDocument(caseId, document, indexName);
    }

}
