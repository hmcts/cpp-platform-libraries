package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.lang.String.format;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.WAIT_UNTIL;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.xcontent.XContentType.JSON;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchIndexIngestorUtil {

    private ElasticSearchClient elasticSearchClient;
    private ObjectMapper objectMapper;
    private RestHighLevelClient restClient;


    public ElasticSearchIndexIngestorUtil() {
        this.elasticSearchClient = new ElasticSearchClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void ingestCaseData(final List<? extends BaseCaseDocument> caseData) throws IOException {
        if (CollectionUtils.isNotEmpty(caseData)) {
            restClient = elasticSearchClient.restClient(IndexInfo.findByIngesterClazz(caseData.get(0)));
        }

        final BulkRequest bulkRequest = new BulkRequest();
        caseData.stream()
                .map(caseDataObject-> toIndexRequest(IndexInfo.findByIngesterClazz(caseDataObject), caseDataObject))
                .filter(Objects::nonNull)
                .forEach(bulkRequest::add);

        doBulkRequest(bulkRequest);
    }

    private void doBulkRequest(final BulkRequest bulkRequest) throws IOException {
        bulkRequest.setRefreshPolicy(WAIT_UNTIL);

        try {
            final BulkResponse response = restClient.bulk(bulkRequest, DEFAULT);

            if (response.hasFailures()) {
                throw new RuntimeException(format("BulkRequest failed: %s", response.buildFailureMessage()));
            }
        } finally {
            restClient.close();
        }
    }

    @SuppressWarnings("squid:S3011")
    private IndexRequest toIndexRequest(final IndexInfo indexInfo, final BaseCaseDocument caseDocument) {
        final String caseId = caseDocument.getCaseId();
        final IndexRequest request = new IndexRequest(indexInfo.getIndexName());
        request.id(caseId);
        request.source(toJsonString(caseDocument), JSON);
        return request;
    }

    private String toJsonString(final Object caseDocument) {
        try {
            return objectMapper.writeValueAsString(caseDocument);
        } catch (final JsonProcessingException jpEx) {
            throw new RuntimeException("Couldn't convert to JSON !", jpEx);
        }
    }

}
