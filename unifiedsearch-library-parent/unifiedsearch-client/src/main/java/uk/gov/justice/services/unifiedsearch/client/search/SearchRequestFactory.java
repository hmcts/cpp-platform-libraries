package uk.gov.justice.services.unifiedsearch.client.search;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;

import uk.gov.justice.services.common.configuration.GlobalValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.SortOptions;

@ApplicationScoped
public class SearchRequestFactory {

    @Inject
    @GlobalValue(key = "unifiedSearchMaxQueryResultSize", defaultValue = "100")
    private String maxQueryResultSize;


    public SearchRequest getSearchRequestBy(final Query.Builder queryBuilder, final String indexName, final int pageSize, final int startFrom,
                                            final SortOptions sortOptions) {

        checkValid(pageSize, startFrom);

        return SearchRequest.of(s -> {
            s.index(indexName)
                    .query(queryBuilder.build())
                    .size(pageSize)
                    .from(startFrom)
                    .trackTotalHits(t -> t.enabled(true));

            if (sortOptions != null) {
                s.sort(sortOptions);
            }

            return s;
        });
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
