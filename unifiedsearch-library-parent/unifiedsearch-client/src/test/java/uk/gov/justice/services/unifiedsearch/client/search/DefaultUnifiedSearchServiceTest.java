package uk.gov.justice.services.unifiedsearch.client.search;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CPS_CASE;

import java.util.stream.Stream;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultUnifiedSearchServiceTest {

    @Mock
    private StringToJsonObjectConverter stringToJsonObjectConverter;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private SearchRequestFactory searchRequestFactory;

    @Mock
    private SearchResponse searchResponse;

    @Mock
    private SearchRequest searchRequest;

    @Mock
    private Query.Builder query;

    @Mock
    private HitsMetadata searchHits;

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

        when(elasticsearchClient.get(any(GetRequest.class))).thenReturn(getResponse);
        when(getResponse.source()).thenReturn("response");
        when(stringToJsonObjectConverter.convert(responseString)).thenReturn(jsonObject);

        final JsonObject caseDetailsJson = defaultUnifiedSearchService.search("123", indexInfo.getIndexName());

        assertThat(caseDetailsJson, is(jsonObject));
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldThrowExceptionOnSearchById(final IndexInfo indexInfo) throws Exception {
        try {
            given(elasticsearchClient.get(any(GetRequest.class))).willAnswer(invocation -> {
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
        final Query.Builder query = mock(Query.Builder.class);
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );

        when(searchRequestFactory.getSearchRequestBy(query, indexInfo.getIndexName(), 10, 100, sortOptions)).thenReturn(searchRequest);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(JsonData.class))).thenReturn(searchResponse);
        when(searchResponse.hits()).thenReturn(searchHits);
        when(searchHits.total()).thenReturn(TotalHits.of(t-> t.value(0).relation(TotalHitsRelation.Eq)));
        when(searchResultConverter.toJsonArray(searchHits.hits(), Object.class)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(query, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, sortOptions);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(query, indexInfo.getIndexName(), 10, 100, sortOptions);
        verify(elasticsearchClient).search(searchRequest, JsonData.class);

        verifyNoMoreInteractions(searchRequestFactory, elasticsearchClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderWithNestedSort(final IndexInfo indexInfo) throws Exception {
        final Query.Builder query = mock(Query.Builder.class);
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f
                        .field("fieldName")
                        .order(SortOrder.Asc)
                        .nested(n -> n
                                .path("nested.sort.path")
                        )
                )
        );

        when(searchRequestFactory.getSearchRequestBy(query, indexInfo.getIndexName(), 10, 100, sortOptions)).thenReturn(searchRequest);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(JsonData.class))).thenReturn(searchResponse);
        when(searchResponse.hits()).thenReturn(searchHits);
        when(searchHits.total()).thenReturn(TotalHits.of(t-> t.value(0).relation(TotalHitsRelation.Eq)));
        when(searchResultConverter.toJsonArray(searchHits.hits(), Object.class)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(query, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, sortOptions);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(query, indexInfo.getIndexName(), 10, 100, sortOptions);
        verify(elasticsearchClient).search(searchRequest, JsonData.class);

        verifyNoMoreInteractions(searchRequestFactory, elasticsearchClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderForInnerHitsAsWell(final IndexInfo indexInfo) throws Exception {
        final Query.Builder queryBuilder = mock(Query.Builder.class);
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, sortOptions)).thenReturn(searchRequest);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(JsonData.class))).thenReturn(searchResponse);
        when(searchResponse.hits()).thenReturn(searchHits);
        when(searchHits.total()).thenReturn(TotalHits.of(t-> t.value(0).relation(TotalHitsRelation.Eq)));
        when(searchResultConverter.toJsonArray(searchHits.hits(), Object.class)).thenReturn(hitsAsJsonArray);
        when(searchResultConverter.convertInnerHitsToJsonArray(searchHits.hits(), Object.class, resultInnerHitNodeName)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, sortOptions, Object.class, resultInnerHitNodeName);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, sortOptions);
        verify(elasticsearchClient).search(searchRequest, JsonData.class);

        verifyNoMoreInteractions(searchRequestFactory, elasticsearchClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldSearchIndexByQueryBuilderForInnerHitsAsWellWithNestedSort(final IndexInfo indexInfo) throws Exception {
        final Query.Builder queryBuilder = mock(Query.Builder.class);
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f
                        .field("fieldName")
                        .order(SortOrder.Asc)
                        .nested(n -> n
                                .path("nested.sort.path")
                        )
                )
        );

        when(searchRequestFactory.getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, sortOptions)).thenReturn(searchRequest);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(JsonData.class))).thenReturn(searchResponse);
        when(searchResponse.hits()).thenReturn(searchHits);
        when(searchHits.total()).thenReturn(TotalHits.of(t-> t.value(0).relation(TotalHitsRelation.Eq)));
        when(searchResultConverter.toJsonArray(searchHits.hits(), Object.class)).thenReturn(hitsAsJsonArray);
        when(searchResultConverter.convertInnerHitsToJsonArray(searchHits.hits(), Object.class, resultInnerHitNodeName)).thenReturn(hitsAsJsonArray);

        final JsonObject actualResponse = defaultUnifiedSearchService.search(queryBuilder, indexInfo.getIndexName(), Object.class, resultHitNodeName, 10, 100, sortOptions, Object.class, resultInnerHitNodeName);

        assertThat(actualResponse.getInt("totalResults"), is(0));
        assertThat(actualResponse.getJsonArray(resultHitNodeName), hasSize(0));

        verify(searchRequestFactory).getSearchRequestBy(queryBuilder, indexInfo.getIndexName(), 10, 100, sortOptions);
        verify(elasticsearchClient).search(searchRequest, JsonData.class);

        verifyNoMoreInteractions(searchRequestFactory, elasticsearchClient, stringToJsonObjectConverter);
    }

    @ParameterizedTest
    @MethodSource("indexInfoDataProvider")
    public void shouldThrowUnifiedSearchClientExceptionOnSearchByQueryBuilder(final IndexInfo indexInfo) {
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );
        given(searchRequestFactory.getSearchRequestBy(any(Query.Builder.class), any(String.class), anyInt(), anyInt(), any(SortOptions.class))).willAnswer(invocation -> {
            throw new IOException("oops");
        });

        final UnifiedSearchClientException unifiedSearchClientException = assertThrows(UnifiedSearchClientException.class, () ->
                defaultUnifiedSearchService.search(
                        query,
                        indexInfo.getIndexName(),
                        Object.class,
                        resultHitNodeName,
                        10,
                        100,
                        sortOptions));

        assertThat(unifiedSearchClientException.getMessage(), is("Unable to perform search by QueryBuilder"));
    }
}