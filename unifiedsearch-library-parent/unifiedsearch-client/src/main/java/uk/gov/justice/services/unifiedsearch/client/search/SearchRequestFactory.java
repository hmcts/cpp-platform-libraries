package uk.gov.justice.services.unifiedsearch.client.search;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import uk.gov.justice.services.common.configuration.GlobalValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

@ApplicationScoped
public class SearchRequestFactory {

    @Inject
    @GlobalValue(key = "unifiedSearchMaxQueryResultSize", defaultValue = "100")
    private String maxQueryResultSize;


    public SearchRequest getSearchRequestBy(final QueryBuilder queryBuilder, final String indexName, final int pageSize, final int startFrom,
                                            final FieldSortBuilder fieldSortBuilder) {

        checkValid(pageSize, startFrom);

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from(startFrom);
        /* Forcing ES to accurately count like in ES 6.x
         See https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        */
        searchSourceBuilder.trackTotalHits(true);

        if (nonNull(fieldSortBuilder)) {
            searchSourceBuilder.sort(fieldSortBuilder);
        }

        final SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }


    private void checkValid(final int pageSize, final int startFrom) {

        final int maxResultSize = valueOf(maxQueryResultSize);

        if (pageSize < 0 || pageSize > maxResultSize) {
            throw new IllegalArgumentException(
                    format("Provided 'pageSize' param [%s] must be between 0 and %s", pageSize, maxResultSize));
        }
        if (startFrom < 0) {
            throw new IllegalArgumentException(
                    format("Provided 'startFrom' param [%s] must be greater or equal to 0", startFrom));
        }

    }
}
