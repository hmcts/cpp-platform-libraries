package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

public class ElasticSearchIndexFinderUtil {

    private ElasticSearchClient elasticSearchClient;

    public ElasticSearchIndexFinderUtil(final ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public JsonObject findAll(final String indexName) throws IOException {
        Query.Builder builder = new Query.Builder();
        builder.matchAll(m -> m);
        return findBy(builder, indexName);
    }

    public JsonObject findBy(final Query.Builder queryBuilder, final String indexName) throws IOException {
        final SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(indexName)
                .query(queryBuilder.build())
                .size(10_000)
        );

        final ElasticsearchClient restClient = elasticSearchClient.restClient(IndexInfo.findByIndexName(indexName));
        try {
            final SearchResponse result = restClient.search(searchRequest, JsonData.class);
            final JsonArray jsonArray = toJsonArray(result.hits().hits());
            return getJsonBuilderFactory().createObjectBuilder()
                    .add("index", jsonArray)
                    .add("totalResults", jsonArray.size())
                    .build();

        } finally {
            restClient.close();
        }
    }


    public JsonObject findByCaseIds(final String indexName, final String... caseIds) throws IOException {
        Query.Builder builder = new Query.Builder();
        builder.terms(t -> t
                .field("caseId")
                .terms(v -> v.value(
                        Arrays.stream(caseIds)
                                .map(FieldValue::of)
                                .toList()
                )));

        return findBy(builder, indexName);
    }

    private JsonArray toJsonArray(final List<Hit<Object>> searchHits) {
        final JsonArrayBuilder results = getJsonBuilderFactory().createArrayBuilder();
        searchHits.forEach(searchHit -> results.add(searchHit.source().toString()));
        return results.build();
    }

}
