package uk.gov.justice.services.unifiedsearch.client.search;

import javax.json.JsonObject;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

public interface UnifiedSearchService {

    JsonObject search(final String documentId, final String indexName);

    JsonObject search(final QueryBuilder queryBuilder, final String indexName, final Class<?> resultHitType,
                      final String resultHitNodeName, final int pageSize, final int startFrom,
                      final FieldSortBuilder fieldSortBuilder);

    JsonObject search(final QueryBuilder queryBuilder, final String indexName, final Class<?> resultHitType,
                      final String resultHitNodeName, final int pageSize, final int startFrom,
                      final FieldSortBuilder fieldSortBuilder, final Class<?> innerHitResultType,
                      final String innerResultHightNodeName);
}
