package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.lang.String.format;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.CrimeIndexConstants.ES_CRIME_CASE_INDEX_NAME;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;

public class ElasticSearchIndexRemoverUtil {

    private ElasticSearchClient elasticSearchClient;
    private ElasticSearchIndexCreatorUtil elasticSearchIndexCreatorUtil;

    public ElasticSearchIndexRemoverUtil() {
        this.elasticSearchClient = new ElasticSearchClient();
        this.elasticSearchIndexCreatorUtil = new ElasticSearchIndexCreatorUtil();
    }

    public void deleteAndCreateCaseIndex() throws IOException {
        deleteAndCreateCaseIndex(ES_CRIME_CASE_INDEX_NAME);
    }

    public void deleteAndCreateCaseIndex(final String indexName) throws IOException {
        deleteCaseIndex(indexName);
        createCaseIndex(indexName);
    }

    protected void createCaseIndex(final String indexName) throws IOException {
        elasticSearchIndexCreatorUtil.createCaseIndex(indexName);
    }

    public void deleteCaseIndex(final String indexName) throws IOException {
        deleteIndex(indexName);
    }

    public boolean deleteIndex(final String indexName) throws IOException {
        ElasticsearchClient adminRestClient = null;

        try {
            DeleteIndexRequest request = DeleteIndexRequest.of(d -> d
                    .index(indexName)
                    .timeout(t -> t.time("2m"))
            );
            adminRestClient = elasticSearchClient.adminRestClient(IndexInfo.findByIndexName(indexName));

            final DeleteIndexResponse response = adminRestClient.indices().delete(request);
            if (response.acknowledged()) {
                return true;
            }

        } catch (final ElasticsearchException exception) {
            if (exception.response() != null
                    && exception.response().status() == 404){
                //This is OK
            } else {
                throw new RuntimeException(format("Failed to delete index: %s", indexName), exception);
            }

        } finally {
            if(adminRestClient != null) {
                adminRestClient.close();
            }
        }
        return false;
    }
}
