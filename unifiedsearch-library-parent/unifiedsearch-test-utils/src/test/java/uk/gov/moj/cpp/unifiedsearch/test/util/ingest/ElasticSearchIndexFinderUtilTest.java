package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonNumber;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import java.util.List;
import java.util.stream.Stream;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import javax.json.JsonNumber;
import javax.json.JsonObject;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ElasticSearchIndexFinderUtilTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ElasticSearchClient elasticSearchClient;

    @InjectMocks
    private ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtil;

    private static Stream<Arguments> provideCaseIndex() {
        return Stream.of(
                Arguments.of(CRIME_CASE, CPS_CASE)
        );
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        elasticSearchIndexFinderUtil = new ElasticSearchIndexFinderUtil(elasticSearchClient);
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldFindBy(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtil = new ElasticSearchIndexFinderUtil(elasticSearchClient);
        final String testJsonString = "test";

        final SearchResponse searchResponse = mock(SearchResponse.class);
        final Hit<Object> searchHit = mock(Hit.class);

        final HitsMetadata<Object> searchHits = HitsMetadata.<Object>of(hm -> hm
                .hits(List.of(searchHit))
                .total(new TotalHits.Builder()
                        .value(1L)
                        .relation(TotalHitsRelation.Eq)
                        .build())
                .maxScore(1.0)
        );

        when(searchHit.source()).thenReturn(testJsonString);
        when(elasticSearchClient.restClient(indexInfo).search(any(SearchRequest.class), eq(JsonData.class))).thenReturn(searchResponse);
        when(searchResponse.hits()).thenReturn(searchHits);

        final Query.Builder queryBuilder = mock(Query.Builder.class);
        final JsonObject jsonObject = elasticSearchIndexFinderUtil.findBy(queryBuilder, indexInfo.getIndexName());
        final JsonNumber totalResults = getJsonNumber(jsonObject, "totalResults").get();

        verify(elasticSearchClient.restClient(indexInfo)).search(any(SearchRequest.class), eq(JsonData.class));
        assertThat(totalResults.intValue(), is(1));
        assertThat(jsonObject.getJsonArray("index").getJsonString(0).getString(), is(testJsonString));
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldFindByCaseIds(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtilSpy = spy(elasticSearchIndexFinderUtil);
        elasticSearchIndexFinderUtilSpy.findByCaseIds(indexInfo.getIndexName(), "01");
        verify(elasticSearchIndexFinderUtilSpy).findBy(any(Query.Builder.class), eq(indexInfo.getIndexName()));
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldFindAll(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtilSpy = spy(elasticSearchIndexFinderUtil);
        elasticSearchIndexFinderUtilSpy.findAll(indexInfo.getIndexName());
        verify(elasticSearchIndexFinderUtilSpy).findBy(any(Query.Builder.class), eq(indexInfo.getIndexName()));
    }
}