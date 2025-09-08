package uk.gov.justice.services.unifiedsearch.client.search;

import static java.lang.String.format;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CPS_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultUnifiedSearchServiceTest {

    @Mock
    private StringToJsonObjectConverter stringToJsonObjectConverter;

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchRequestFactory searchRequestFactory;

    @Mock
    private SearchResponse searchResponse;

    @Mock
    private SearchRequest searchRequest;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private SearchHits searchHits;

    @Mock
    private TotalHits totalHits;

    @Mock
    private SearchResultConverter searchResultConverter;

    @Mock
    private JsonArray hitsAsJsonArray;

    @InjectMocks
    private DefaultUnifiedSearchService defaultUnifiedSearchService;

    private String resultHitNodeName;

    private String resultInnerHitNodeName;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resultHitNodeName = "cases";
        resultInnerHitNodeName = "parties";
        when(searchHits.getTotalHits()).thenReturn(totalHits);
    }

    private static Stream<Arguments> indexInfoDataProvider() {
        return Stream.of(
                Arguments.of(CPS_CASE)
        );
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchById(final IndexInfo indexInfo) throws Exception {
        final String responseString = "response";

        final GetResponse getResponse = mock(GetResponse.class);
        final JsonObject jsonObject = mock(JsonObject.class);

        when(restHighLevelClient.get(any(GetRequest.class), any(RequestOptions.class))).thenReturn(getResponse);
        when(getResponse.getSourceAsString()).thenReturn("response");
        when(stringToJsonObjectConverter.convert(responseString)).thenReturn(jsonObject);

        final JsonObject caseDetailsJson = defaultUnifiedSearchService.search("123", indexInfo.getIndexName());

        assertThat(caseDetailsJson, is(jsonObject));
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldThrowExceptionOnSearchById(final IndexInfo indexInfo) throws Exception {
        try {
            given(restHighLevelClient.get(any(GetRequest.class), any(RequestOptions.class))).willAnswer(invocation -> {
                throw new IOException("oops");
            });
            defaultUnifiedSearchService.search("123", indexInfo.getIndexName());
            fail();
        } catch (final UnifiedSearchClientException expected) {
            assertThat(expected.getMessage(), is(format("Unable to perform search for documentId 123 on index %s", indexInfo.getIndexName())));
        }
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilder(final IndexInfo indexInfo) throws Exception {
        final QueryBuilder queryBuilder = mock(QueryBuilder.class);
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder)).thenReturn(searchRequest);
        when(restHighLevelClient.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchResultConverter.toJsonArray(searchHits, Object.class)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, fieldSortBuilder);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder);
        verify(restHighLevelClient).search(searchRequest, RequestOptions.DEFAULT);

        verifyNoMoreInteractions(searchRequestFactory, restHighLevelClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderWithNestedSort(final IndexInfo indexInfo) throws Exception {
        final QueryBuilder queryBuilder = mock(QueryBuilder.class);
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC).setNestedSort(new NestedSortBuilder("nested.sort.path"));

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder)).thenReturn(searchRequest);
        when(restHighLevelClient.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchResultConverter.toJsonArray(searchHits, Object.class)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, fieldSortBuilder);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder);
        verify(restHighLevelClient).search(searchRequest, RequestOptions.DEFAULT);

        verifyNoMoreInteractions(searchRequestFactory, restHighLevelClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderForInnerHitsAsWell(final IndexInfo indexInfo) throws Exception {
        final QueryBuilder queryBuilder = mock(QueryBuilder.class);
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder)).thenReturn(searchRequest);
        when(restHighLevelClient.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchResultConverter.toJsonArray(searchHits, Object.class)).thenReturn(hitsAsJsonArray);
        when(searchResultConverter.convertInnerHitsToJsonArray(searchHits, Object.class, resultInnerHitNodeName)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, fieldSortBuilder, Object.class, resultInnerHitNodeName);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder);
        verify(restHighLevelClient).search(searchRequest, RequestOptions.DEFAULT);

        verifyNoMoreInteractions(searchRequestFactory, restHighLevelClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderForInnerHitsAsWellWithNestedSort(final IndexInfo indexInfo) throws Exception {
        final QueryBuilder queryBuilder = mock(QueryBuilder.class);
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC).setNestedSort(new NestedSortBuilder("nested.sort.path"));

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder)).thenReturn(searchRequest);
        when(restHighLevelClient.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchResultConverter.toJsonArray(searchHits, Object.class)).thenReturn(hitsAsJsonArray);
        when(searchResultConverter.convertInnerHitsToJsonArray(searchHits, Object.class, resultInnerHitNodeName)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, fieldSortBuilder, Object.class, resultInnerHitNodeName);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, fieldSortBuilder);
        verify(restHighLevelClient).search(searchRequest, RequestOptions.DEFAULT);

        verifyNoMoreInteractions(searchRequestFactory, restHighLevelClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldThrowUnifiedSearchClientExceptionOnSearchByQueryBuilder(final IndexInfo indexInfo) {
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);
        given(searchRequestFactory.getSearchRequestBy(any(QueryBuilder.class), any(String.class), anyInt(), anyInt(), any(FieldSortBuilder.class))).willAnswer(invocation -> {
            throw new IOException("oops");
        });

        final UnifiedSearchClientException unifiedSearchClientException = assertThrows(UnifiedSearchClientException.class, () ->
                defaultUnifiedSearchService.search(
                        queryBuilder,
                        indexInfo.getIndexName(),
                        Object.class,
                        resultHitNodeName,
                        10,
                        100,
                        fieldSortBuilder));

        assertThat(unifiedSearchClientException.getMessage(), is("Unable to perform search by QueryBuilder"));
    }
}