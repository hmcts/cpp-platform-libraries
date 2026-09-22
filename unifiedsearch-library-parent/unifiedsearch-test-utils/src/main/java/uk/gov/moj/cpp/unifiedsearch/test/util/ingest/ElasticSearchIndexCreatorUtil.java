package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;
import java.io.StringReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.apache.commons.io.IOUtils;

public class ElasticSearchIndexCreatorUtil {

    private ElasticSearchClient elasticSearchClient;

    public ElasticSearchIndexCreatorUtil() {
        this.elasticSearchClient = new ElasticSearchClient();
    }

    public ElasticSearchIndexCreatorUtil(final ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public boolean createCaseIndex(final String indexName) throws IOException {
        final IndexInfo indexInfo = IndexInfo.findByIndexName(indexName);
        final String sourceConfig = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(indexInfo.getIndexSourceFile()));
        return createIndex(indexName, sourceConfig);
    }

    private boolean createIndex(final String indexName, final String sourceConfig) throws IOException {
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .withJson(new StringReader(sourceConfig))
                .timeout(t -> t.time("2m"))
        );

        final ElasticsearchClient adminRestClient = elasticSearchClient.adminRestClient(IndexInfo.findByIndexName(indexName));
        try {
            final CreateIndexResponse response = adminRestClient.indices().create(request);

            return response.acknowledged();
        } finally {
            adminRestClient.close();
        }
    }
}


