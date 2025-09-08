package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ElasticSearchIndexCreatorUtilTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
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

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldCreateCaseIndexWithIndexName(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexCreatorUtil elasticSearchIndexCreatorUtil = new ElasticSearchIndexCreatorUtil(elasticSearchClient);
        final CreateIndexResponse createIndexResponse = mock(CreateIndexResponse.class);

        when(createIndexResponse.isAcknowledged()).thenReturn(true);
        when(elasticSearchClient.adminRestClient(indexInfo).indices().create(any(CreateIndexRequest.class), any(RequestOptions.class))).thenReturn(createIndexResponse);

        final boolean caseIndex = elasticSearchIndexCreatorUtil.createCaseIndex(indexInfo.getIndexName());

        assertThat(caseIndex, is(true));
    }

}