package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
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

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import javax.json.JsonNumber;
import javax.json.JsonObject;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
        final SearchHit searchHit = mock(SearchHit.class);
        final SearchHits searchHits = new SearchHits(new SearchHit[]{searchHit}, new TotalHits(1, EQUAL_TO), 1F);

        when(elasticSearchClient.restClient(indexInfo).search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
        when(searchHit.getSourceAsString()).thenReturn(testJsonString);
        when(searchResponse.getHits()).thenReturn(searchHits);

        final QueryBuilder queryBuilder = mock(QueryBuilder.class);
        final JsonObject jsonObject = elasticSearchIndexFinderUtil.findBy(queryBuilder, indexInfo.getIndexName());
        final JsonNumber totalResults = getJsonNumber(jsonObject, "totalResults").get();

        verify(elasticSearchClient.restClient(indexInfo)).search(any(SearchRequest.class), eq(DEFAULT));
        assertThat(totalResults.intValue(), is(1));
        assertThat(jsonObject.getJsonArray("index").getJsonString(0).getString(), is(testJsonString));
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldFindByCaseIds(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtilSpy = spy(elasticSearchIndexFinderUtil);
        elasticSearchIndexFinderUtilSpy.findByCaseIds(indexInfo.getIndexName(), "01");
        verify(elasticSearchIndexFinderUtilSpy).findBy(any(TermsQueryBuilder.class), eq(indexInfo.getIndexName()));
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldFindAll(final IndexInfo indexInfo) throws IOException {
        final ElasticSearchIndexFinderUtil elasticSearchIndexFinderUtilSpy = spy(elasticSearchIndexFinderUtil);
        elasticSearchIndexFinderUtilSpy.findAll(indexInfo.getIndexName());
        verify(elasticSearchIndexFinderUtilSpy).findBy(any(MatchAllQueryBuilder.class), eq(indexInfo.getIndexName()));
    }
}