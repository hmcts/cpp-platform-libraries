package uk.gov.justice.services.unifiedsearch.client.search;

import static org.elasticsearch.search.sort.SortBuilders.fieldSort;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.junit.jupiter.api.Test;

public class SearchRequestFactoryTest {

    private SearchRequestFactory searchRequestFactory = new SearchRequestFactory();


    @Test
    public void shouldCreateSearchRequestWithoutSorting() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", 10, 100, null);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.indices().length, is(1));
        assertThat(searchRequest.indices()[0], is("indexName"));
        assertThat(searchRequest.source().size(), is(10));
        assertThat(searchRequest.source().from(), is(100));
        assertThat(searchRequest.source().sorts(), is(nullValue()));
    }


    @Test
    public void shouldCreateSearchRequest() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", 10, 100, fieldSortBuilder);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.indices().length, is(1));
        assertThat(searchRequest.indices()[0], is("indexName"));
        assertThat(searchRequest.source().size(), is(10));
        assertThat(searchRequest.source().from(), is(100));
        assertThat(searchRequest.source().sorts(), hasSize(1));
        assertThat(searchRequest.source().sorts().get(0).order(), is(ASC));
    }

    @Test
    public void shouldCreateSearchRequestWithNestedSort() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC).setNestedSort(new NestedSortBuilder("nested.sort.path"));

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", 10, 100, fieldSortBuilder);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.indices().length, is(1));
        assertThat(searchRequest.indices()[0], is("indexName"));
        assertThat(searchRequest.source().size(), is(10));
        assertThat(searchRequest.source().from(), is(100));
        assertThat(searchRequest.source().sorts(), hasSize(1));
        assertThat(searchRequest.source().sorts().get(0).order(), is(ASC));
        final FieldSortBuilder actualFieldSort = (FieldSortBuilder) searchRequest.source().sorts().get(0);
        assertThat(actualFieldSort.getNestedSort().getPath(), is("nested.sort.path"));

    }

    @Test
    public void shouldFailForTooLargeReturnSize() {
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);
        setField(searchRequestFactory, "maxQueryResultSize", "10");

        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", 1000, 100, fieldSortBuilder));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'pageSize' param [1000] must be between 0 and 10"));
    }

    @Test
    public void shouldFailForNegativeReturnSize() {
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);
        setField(searchRequestFactory, "maxQueryResultSize", "10");

        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", -10, 100, fieldSortBuilder));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'pageSize' param [-10] must be between 0 and 10"));
    }

    @Test
    public void shouldFailForInvalidFromValue() {
        final FieldSortBuilder fieldSortBuilder = fieldSort("fieldName").order(ASC);
        setField(searchRequestFactory, "maxQueryResultSize", "100");


        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(QueryBuilder.class), "indexName", 10, -10, fieldSortBuilder));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'startFrom' param [-10] must be greater or equal to 0"));
    }
}
