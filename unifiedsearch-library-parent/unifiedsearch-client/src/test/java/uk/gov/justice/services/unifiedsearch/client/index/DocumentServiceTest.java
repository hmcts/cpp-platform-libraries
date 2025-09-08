package uk.gov.justice.services.unifiedsearch.client.index;

import static java.util.UUID.randomUUID;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CPS_CASE;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CRIME_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.services.unifiedsearch.client.factory.GetRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.IndexRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.factory.UpdateRequestFactory;
import uk.gov.justice.services.unifiedsearch.client.restclient.UnifiedSearchHighLevelRestClientProducer;
import uk.gov.justice.services.unifiedsearch.client.retry.IngestionResponseVerifier;
import uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsTransformer;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;

import java.util.UUID;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DocumentServiceTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IndexRequestFactory indexRequestFactory;

    @Mock
    private UpdateRequestFactory updateRequestFactory;

    @Mock
    private GetRequestFactory getRequestFactory;

    @Mock
    private IngestionResponseVerifier ingestionResponseVerifier;

    @Mock
    private CaseDetailsTransformer caseDetailsTransformer;

    @Mock
    private UnifiedSearchHighLevelRestClientProducer highLevelRestClientProvider;

    @InjectMocks
    private CrimeCaseDocumentService documentService;

    private static Stream<Arguments> indexInfoDataProvider() {
        return Stream.of(
                Arguments.of(CRIME_CASE, CPS_CASE)
        );
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldGetDocument(final IndexInfo indexInfo) throws Exception {

        final UUID caseId = randomUUID();

        when(highLevelRestClientProvider.getWriteHighLevelClient()).thenReturn(restHighLevelClient);

        final GetRequest getRequest = getRequestFactory.getRequest(indexInfo.getIndexName(), caseId);
        final GetResponse getResponse = restHighLevelClient.get(getRequest, DEFAULT);

        assertThat(documentService.getDocument(caseId, indexInfo.getIndexName()), is(getResponse));
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldCreateDocumentAndCheckForSuccess(final IndexInfo indexInfo) throws Exception {

        final UUID caseId = randomUUID();
        final long sequenceNumber = 9283L;
        final long primaryTerm = 2347l;

        final JsonObject document = mock(JsonObject.class);
        final IndexRequest indexRequest = mock(IndexRequest.class);
        final IndexResponse indexResponse = mock(IndexResponse.class);

        when(indexRequestFactory.indexRequest(
                indexInfo.getIndexName(),
                document,
                sequenceNumber,
                primaryTerm)).thenReturn(indexRequest);
        when(highLevelRestClientProvider.getWriteHighLevelClient()).thenReturn(restHighLevelClient);
        when(restHighLevelClient.index(indexRequest, DEFAULT)).thenReturn(indexResponse);

        documentService.createDocument(document, caseId, sequenceNumber, primaryTerm, indexInfo.getIndexName());

        verify(ingestionResponseVerifier).checkCreateSucceeded(caseId, indexResponse);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldUpdateDocumentAndCheckForSuccess(final IndexInfo indexInfo) throws Exception {
        final UUID caseId = randomUUID();

        final GetResponse getResponse = mock(GetResponse.class);
        final JsonObject document = mock(JsonObject.class);
        final UpdateResponse updateResponse = mock(UpdateResponse.class);

        when(highLevelRestClientProvider.getWriteHighLevelClient()).thenReturn(restHighLevelClient);
        when(restHighLevelClient.get(any(), eq(DEFAULT))).thenReturn(getResponse);
        when(restHighLevelClient.update(any(), eq(DEFAULT))).thenReturn(updateResponse);

        documentService.upsertDocument(
                caseId,
                document,
                indexInfo.getIndexName());

        verify(ingestionResponseVerifier).checkUpsertSucceeded(caseId, updateResponse);
    }
}
