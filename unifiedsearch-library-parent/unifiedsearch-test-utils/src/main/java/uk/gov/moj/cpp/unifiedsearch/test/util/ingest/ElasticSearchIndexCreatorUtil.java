package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.xcontent.XContentType.JSON;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.core.TimeValue;

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
        final CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.setTimeout(TimeValue.timeValueMinutes(2));
        request.source(sourceConfig, JSON);
        final RestHighLevelClient adminRestClient = elasticSearchClient.adminRestClient(IndexInfo.findByIndexName(indexName));
        try {
            final CreateIndexResponse response = adminRestClient.indices().create(request, DEFAULT);

            return response.isAcknowledged();
        } finally {
            adminRestClient.close();
        }
    }
}


