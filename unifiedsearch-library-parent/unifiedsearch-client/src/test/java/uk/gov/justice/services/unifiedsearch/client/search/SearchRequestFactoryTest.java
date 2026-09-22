package uk.gov.justice.services.unifiedsearch.client.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.SortOptions;
import org.junit.jupiter.api.Test;

public class SearchRequestFactoryTest {

    private SearchRequestFactory searchRequestFactory = new SearchRequestFactory();


    @Test
    public void shouldCreateSearchRequestWithoutSorting() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", 10, 100, null);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.index().size(), is(1));
        assertThat(searchRequest.index().get(0), is("indexName"));
        assertThat(searchRequest.size(), is(10));
        assertThat(searchRequest.from(), is(100));
        assertThat(searchRequest.sort().isEmpty(), is(true));
    }


    @Test
    public void shouldCreateSearchRequest() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", 10, 100, sortOptions);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.index().size(), is(1));
        assertThat(searchRequest.index().get(0), is("indexName"));
        assertThat(searchRequest.size(), is(10));
        assertThat(searchRequest.from(), is(100));
        assertThat(searchRequest.sort(), hasSize(1));
        assertThat(searchRequest.sort().get(0).field().order(), is(SortOrder.Asc));
    }

    @Test
    public void shouldCreateSearchRequestWithNestedSort() {

        setField(searchRequestFactory, "maxQueryResultSize", "100");
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f
                        .field("fieldName")
                        .order(SortOrder.Asc)
                        .nested(n -> n
                                .path("nested.sort.path")
                        )
                )
        );

        final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", 10, 100, sortOptions);

        assertThat(searchRequest, notNullValue());
        assertThat(searchRequest.index().size(), is(1));
        assertThat(searchRequest.index().get(0), is("indexName"));
        assertThat(searchRequest.size(), is(10));
        assertThat(searchRequest.from(), is(100));
        assertThat(searchRequest.sort(), hasSize(1));
        assertThat(searchRequest.sort().get(0).field().order(), is(SortOrder.Asc));
        assertThat(searchRequest.sort().get(0).field().nested().path(), is("nested.sort.path"));

    }

    @Test
    public void shouldFailForTooLargeReturnSize() {
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );
        setField(searchRequestFactory, "maxQueryResultSize", "10");

        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", 1000, 100, sortOptions));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'pageSize' param [1000] must be between 0 and 10"));
    }

    @Test
    public void shouldFailForNegativeReturnSize() {
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );

        setField(searchRequestFactory, "maxQueryResultSize", "10");

        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", -10, 100, sortOptions));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'pageSize' param [-10] must be between 0 and 10"));
    }

    @Test
    public void shouldFailForInvalidFromValue() {
        final SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f.field("fieldName").order(SortOrder.Asc))
        );
        setField(searchRequestFactory, "maxQueryResultSize", "100");


        final IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> searchRequestFactory.getSearchRequestBy(mock(Query.Builder.class), "indexName", 10, -10, sortOptions));

        assertThat(illegalArgumentException.getMessage(), is("Provided 'startFrom' param [-10] must be greater or equal to 0"));
    }
}
