package uk.gov.justice.services.unifiedsearch.client.search;

import javax.json.JsonObject;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

public interface UnifiedSearchService {

    JsonObject search(final String documentId, final String indexName);

    JsonObject search(final Query.Builder queryBuilder, final String indexName, final Class<?> resultHitType,
                      final String resultHitNodeName, final int pageSize, final int startFrom,
                      final SortOptions sortOptions);

    JsonObject search(final Query.Builder queryBuilder, final String indexName, final Class<?> resultHitType,
                      final String resultHitNodeName, final int pageSize, final int startFrom,
                      final SortOptions sortOptions, final Class<?> innerHitResultType,
                      final String innerResultHightNodeName);
}
