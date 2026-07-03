package uk.gov.justice.services.unifiedsearch.client.search;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.json.JsonData;

import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.restclient.UnifiedSearchHighLevelRestClientProducer;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;

import static java.lang.String.format;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;

@ApplicationScoped
public class DefaultUnifiedSearchService implements UnifiedSearchService {

    private static final String TOTAL_RESULTS_NODE_NAME = "totalResults";

    @Inject
    @Named(CPS_READ_USER)
    private ElasticsearchClient cpsCaseElasticsearchClient;

    @Inject
    @Named(READ_USER)
    private ElasticsearchClient crimeCaseElasticsearchClient;

    @Inject
    private StringToJsonObjectConverter stringToJsonObjectConverter;

    @Inject
    private UnifiedSearchHighLevelRestClientProducer highLevelRestClientProvider;

    @Inject
    private SearchRequestFactory searchRequestFactory;

    @Inject
    private SearchResultConverter searchResultConverter;

    public JsonObject search(final String documentId, final String indexName) {
        try {
            final GetRequest getRequest = GetRequest.of(r -> r.index(indexName).id(documentId));
            final ElasticsearchClient elasticsearchClient = restHighLevelClient(indexName);
            final GetResponse getResponse = elasticsearchClient.get(getRequest);

            return stringToJsonObjectConverter.convert(getResponse.source().toString());
        } catch (final IOException ioe) {
            throw new UnifiedSearchClientException(format("Unable to perform search for documentId %s on index %s", documentId, indexName), ioe);
        }
    }

    @Override
    public JsonObject search(final Query.Builder queryBuilder, final String indexName, final Class<?> resultHitType,
                             final String resultHitNodeName, final int pageSize, final int startFrom,
                             final SortOptions sortOptions) {
        return search(queryBuilder, indexName, resultHitType, resultHitNodeName, pageSize, startFrom, sortOptions, null, null);
    }

    @Override
    public JsonObject search(final Query.Builder queryBuilder, final String indexName, final Class<?> resultHitType,
                             final String resultHitNodeName, final int pageSize, final int startFrom,
                             final SortOptions sortOptions, final Class<?> innerHitResultType,
                             final String innerResultHightNodeName) {
        try {

            final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(queryBuilder, indexName, pageSize, startFrom, sortOptions);
            final ElasticsearchClient elasticsearchClient = restHighLevelClient(indexName);
            final SearchResponse response = elasticsearchClient.search(searchRequest, JsonData.class);
            final JsonArray hitsAsJsonArray = searchResultConverter.toJsonArray(response.hits().hits(), resultHitType);
            final JsonObjectBuilder jsonObjectBuilder = getJsonBuilderFactory().createObjectBuilder()
                    .add(TOTAL_RESULTS_NODE_NAME, response.hits().total().value())
                    .add(resultHitNodeName, hitsAsJsonArray);
            if (null != innerResultHightNodeName) {
                final JsonArray innerHitsAsJsonArray = searchResultConverter.convertInnerHitsToJsonArray(response.hits().hits(),
                        innerHitResultType, innerResultHightNodeName);
                jsonObjectBuilder.add(innerResultHightNodeName, innerHitsAsJsonArray);
            }
            return jsonObjectBuilder.build();

        } catch (final IOException ioe) {
            throw new UnifiedSearchClientException("Unable to perform search by QueryBuilder", ioe);
        }
    }

    private ElasticsearchClient restHighLevelClient(final String indexName) {
        return indexName.equals(IndexInfo.CPS_CASE.getIndexName()) ? cpsCaseElasticsearchClient : crimeCaseElasticsearchClient;
    }
}
