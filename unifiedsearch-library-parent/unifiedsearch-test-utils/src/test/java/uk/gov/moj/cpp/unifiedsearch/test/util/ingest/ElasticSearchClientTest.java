package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class ElasticSearchClientTest {

    @InjectMocks
    private ElasticSearchClient elasticSearchClient;

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
    public void shouldReturnHighLevelRestClient() {
        final RestHighLevelClient restHighLevelClient1 = elasticSearchClient.restClient();
        final RestHighLevelClient restHighLevelClient2 = elasticSearchClient.adminRestClient();

        assertNotNull(restHighLevelClient1);
        assertNotNull(restHighLevelClient2);
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldReturnHighLevelRestClientForCaseIndexes(final IndexInfo indexInfo) {

        final RestHighLevelClient restHighLevelClient1 = elasticSearchClient.restClient(indexInfo);
        final RestHighLevelClient restHighLevelClient2 = elasticSearchClient.adminRestClient(indexInfo);

        assertNotNull(restHighLevelClient1);
        assertNotNull(restHighLevelClient2);
    }
}