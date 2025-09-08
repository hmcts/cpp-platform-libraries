package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class ElasticSearchIndexFinderUtil {

    private ElasticSearchClient elasticSearchClient;

    public ElasticSearchIndexFinderUtil(final ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public JsonObject findAll(final String indexName) throws IOException {
        return findBy(matchAllQuery(), indexName);
    }

    public JsonObject findBy(final QueryBuilder queryBuilder, final String indexName) throws IOException {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(10000);

        final SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        final RestHighLevelClient restClient = elasticSearchClient.restClient(IndexInfo.findByIndexName(indexName));
        try {
            final SearchResponse result = restClient.search(searchRequest, DEFAULT);
            final JsonArray jsonArray = toJsonArray(result.getHits());
            return createObjectBuilder()
                    .add("index", jsonArray)
                    .add("totalResults", jsonArray.size())
                    .build();

        } finally {
            restClient.close();
        }
    }


    public JsonObject findByCaseIds(final String indexName, final String... caseIds) throws IOException {
        return findBy(termsQuery("caseId", caseIds), indexName);
    }

    private JsonArray toJsonArray(final SearchHits searchHits) {
        final JsonArrayBuilder results = createArrayBuilder();
        searchHits.forEach(searchHit -> results.add(searchHit.getSourceAsString()));
        return results.build();
    }

}
