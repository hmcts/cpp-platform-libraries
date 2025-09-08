package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.lang.String.format;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.CrimeIndexConstants.ES_CRIME_CASE_INDEX_NAME;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.rest.RestStatus;

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
        RestHighLevelClient adminRestClient = null;

        try {
            final DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            request.timeout(TimeValue.timeValueMinutes(2));
            adminRestClient = elasticSearchClient.adminRestClient(IndexInfo.findByIndexName(indexName));

            final AcknowledgedResponse response = adminRestClient.indices().delete(request, DEFAULT);
            if (response.isAcknowledged()) {
                return true;
            }

        } catch (final ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
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
