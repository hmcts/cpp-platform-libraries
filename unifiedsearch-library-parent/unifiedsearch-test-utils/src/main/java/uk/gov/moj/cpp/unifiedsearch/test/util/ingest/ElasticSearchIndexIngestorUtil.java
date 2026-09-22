package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.lang.String.format;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;

public class ElasticSearchIndexIngestorUtil {

    private ElasticSearchClient elasticSearchClient;
    private ObjectMapper objectMapper;
    private ElasticsearchClient restClient;


    public ElasticSearchIndexIngestorUtil() {
        this.elasticSearchClient = new ElasticSearchClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void ingestCaseData(final List<? extends BaseCaseDocument> caseData) throws IOException {
        if (CollectionUtils.isNotEmpty(caseData)) {
            restClient = elasticSearchClient.restClient(IndexInfo.findByIngesterClazz(caseData.get(0)));
        }

        final List<BulkOperation> operations = caseData.stream()
                .map(caseDataObject-> {
                    IndexRequest request = toIndexRequest(IndexInfo.findByIngesterClazz(caseDataObject), caseDataObject);
                    if (request == null) return null;
                    return BulkOperation.of(op -> op
                            .index(idx -> idx
                                    .index(request.index())
                                    .id(request.id())
                                    .document(caseDataObject)
                            )
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        final BulkRequest bulkRequest =
                co.elastic.clients.elasticsearch.core.BulkRequest.of(b -> b
                        .refresh(co.elastic.clients.elasticsearch._types.Refresh.WaitFor)
                        .operations(operations)
                );

        doBulkRequest(bulkRequest);
    }

    private void doBulkRequest(final BulkRequest bulkRequest) throws IOException {
        BulkRequest requestWithRefresh = BulkRequest.of(b -> {
            b.refresh(Refresh.WaitFor);
            b.operations(bulkRequest.operations());
            return b;
        });

        try {
            final BulkResponse response = restClient.bulk(requestWithRefresh);

            if (response.errors()) {
                throw new RuntimeException(format("BulkRequest failed: %s",
                        response.items().stream()
                                .filter( res -> Objects.nonNull(res.error()))
                                .map(BulkResponseItem::error)
                                .map(Object::toString)
                                .collect(Collectors.joining(", "))
                ));
            }
        } finally {
            restClient.close();
        }
    }

    @SuppressWarnings("squid:S3011")
    private IndexRequest toIndexRequest(final IndexInfo indexInfo, final BaseCaseDocument caseDocument) {
        final String caseId = caseDocument.getCaseId();

        return IndexRequest.of(i -> i
                .index(indexInfo.getIndexName())
                .id(caseId)
                .document(caseDocument)
        );
    }

}
