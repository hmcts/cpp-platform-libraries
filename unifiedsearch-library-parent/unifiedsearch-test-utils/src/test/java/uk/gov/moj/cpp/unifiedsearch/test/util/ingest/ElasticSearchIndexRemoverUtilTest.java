package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ElasticSearchIndexRemoverUtilTest {

    private static final String CRIME_CASE_INDEX = "crime_case_index";

    @Mock
    private ElasticSearchClient elasticSearchClient;

    @Mock
    private ElasticSearchIndexCreatorUtil elasticSearchIndexCreatorUtil;

    @Mock
    private IndicesClient indicesClient;

    @Mock
    private AcknowledgedResponse acknowledgedResponse;

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @InjectMocks
    private ElasticSearchIndexRemoverUtil elasticSearchIndexRemoverUtil;

    private static Stream<Arguments> provideCaseIndex() {
        return Stream.of(
                Arguments.of(CRIME_CASE, CPS_CASE)
        );
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldDeleteAndCreateCaseIndexForCrimeCase() throws IOException {
        final ElasticSearchIndexRemoverUtil elasticSearchIndexRemoverUtilSpy = spy(ElasticSearchIndexRemoverUtil.class);

        doNothing().when(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(CRIME_CASE.getIndexName());
        doNothing().when(elasticSearchIndexRemoverUtilSpy).createCaseIndex(CRIME_CASE.getIndexName());

        elasticSearchIndexRemoverUtilSpy.deleteAndCreateCaseIndex();

        verify(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(CRIME_CASE.getIndexName());
        verify(elasticSearchIndexRemoverUtilSpy).createCaseIndex(CRIME_CASE.getIndexName());
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldDeleteAndCreateCaseIndex(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexRemoverUtil elasticSearchIndexRemoverUtilSpy = spy(ElasticSearchIndexRemoverUtil.class);

        doNothing().when(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(indexInfo.getIndexName());
        doNothing().when(elasticSearchIndexRemoverUtilSpy).createCaseIndex(indexInfo.getIndexName());

        elasticSearchIndexRemoverUtilSpy.deleteAndCreateCaseIndex(indexInfo.getIndexName());

        verify(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(indexInfo.getIndexName());
        verify(elasticSearchIndexRemoverUtilSpy).createCaseIndex(indexInfo.getIndexName());
    }

    @Test
    public void shouldDeleteAndCreateCaseIndexWithIndexName() throws IOException {
        final ElasticSearchIndexRemoverUtil elasticSearchIndexRemoverUtilSpy = spy(ElasticSearchIndexRemoverUtil.class);

        final String indexName = "dummyName";

        doNothing().when(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(indexName);
        doNothing().when(elasticSearchIndexRemoverUtilSpy).createCaseIndex(indexName);

        elasticSearchIndexRemoverUtilSpy.deleteAndCreateCaseIndex(indexName);

        verify(elasticSearchIndexRemoverUtilSpy).deleteCaseIndex(indexName);
        verify(elasticSearchIndexRemoverUtilSpy).createCaseIndex(indexName);
    }

    @Test
    public void shouldDeleteCaseIndexWithIndexName() throws IOException {

        final String indexName = "dummyName";

        final ElasticSearchIndexRemoverUtil elasticSearchIndexRemoverUtilSpy = spy(ElasticSearchIndexRemoverUtil.class);
        doReturn(true).when(elasticSearchIndexRemoverUtilSpy).deleteIndex(indexName);

        elasticSearchIndexRemoverUtilSpy.deleteCaseIndex(indexName);

        verify(elasticSearchIndexRemoverUtilSpy).deleteIndex(indexName);
    }

    @Test
    public void shouldThrowExceptionOnDeleteIndex() throws IOException {
        when(elasticSearchClient.adminRestClient(any(IndexInfo.class))).thenThrow(ElasticsearchException.class);

        var e = assertThrows(RuntimeException.class, () -> elasticSearchIndexRemoverUtil.deleteIndex(CRIME_CASE_INDEX));

        assertThat(e.getMessage(), is("Failed to delete index: " + CRIME_CASE_INDEX));
    }

    @Test
    public void shouldCreateCaseIndexWithIndexName() throws IOException {
        final String indexName = "dummyName";

        elasticSearchIndexRemoverUtil.createCaseIndex(indexName);
        verify(elasticSearchIndexCreatorUtil).createCaseIndex(indexName);
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldDeleteIndex(final IndexInfo indexInfo) throws IOException {

        when(elasticSearchClient.adminRestClient(any(IndexInfo.class))).thenReturn(restHighLevelClient);
        when(restHighLevelClient.indices()).thenReturn(indicesClient);
        when(indicesClient.delete(any(DeleteIndexRequest.class), eq(DEFAULT))).thenReturn(acknowledgedResponse);
        when(acknowledgedResponse.isAcknowledged()).thenReturn(true);

        final boolean result = elasticSearchIndexRemoverUtil.deleteIndex(indexInfo.getIndexName());

        verify(elasticSearchClient).adminRestClient(indexInfo);

        assertThat(result, is(true));
    }
}