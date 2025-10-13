package uk.gov.justice.services.unifiedsearch.client.search;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
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
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;

@ApplicationScoped
public class DefaultUnifiedSearchService implements UnifiedSearchService {

    private static final String TOTAL_RESULTS_NODE_NAME = "totalResults";

    @Inject
    @Named(CPS_READ_USER)
    private RestHighLevelClient cpsCaseHighLevelClient;

    @Inject
    @Named(READ_USER)
    private RestHighLevelClient crimeCaseHighLevelClient;

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
            final GetRequest getRequest = new GetRequest(indexName).id(documentId);
            final RestHighLevelClient restHighLevelClient = restHighLevelClient(indexName);
            final GetResponse getResponse = restHighLevelClient.get(getRequest, DEFAULT);

            return stringToJsonObjectConverter.convert(getResponse.getSourceAsString());
        } catch (final IOException ioe) {
            throw new UnifiedSearchClientException(format("Unable to perform search for documentId %s on index %s", documentId, indexName), ioe);
        }
    }

    @Override
    public JsonObject search(final QueryBuilder queryBuilder, final String indexName, final Class<?> resultHitType,
                             final String resultHitNodeName, final int pageSize, final int startFrom,
                             final FieldSortBuilder fieldSortBuilder) {
        return search(queryBuilder, indexName, resultHitType, resultHitNodeName, pageSize, startFrom, fieldSortBuilder, null, null);
    }

    @Override
    public JsonObject search(final QueryBuilder queryBuilder, final String indexName, final Class<?> resultHitType,
                             final String resultHitNodeName, final int pageSize, final int startFrom,
                             final FieldSortBuilder fieldSortBuilder, final Class<?> innerHitResultType,
                             final String innerResultHightNodeName) {
        try {

            final SearchRequest searchRequest = searchRequestFactory.getSearchRequestBy(queryBuilder, indexName, pageSize, startFrom, fieldSortBuilder);
            final RestHighLevelClient restHighLevelClient = restHighLevelClient(indexName);
            final SearchResponse response = restHighLevelClient.search(searchRequest, DEFAULT);
            final JsonArray hitsAsJsonArray = searchResultConverter.toJsonArray(response.getHits(), resultHitType);
            final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder()
                    .add(TOTAL_RESULTS_NODE_NAME, response.getHits().getTotalHits().value)
                    .add(resultHitNodeName, hitsAsJsonArray);
            if (null != innerResultHightNodeName) {
                final JsonArray innerHitsAsJsonArray = searchResultConverter.convertInnerHitsToJsonArray(response.getHits(),
                        innerHitResultType, innerResultHightNodeName);
                jsonObjectBuilder.add(innerResultHightNodeName, innerHitsAsJsonArray);
            }
            return jsonObjectBuilder.build();

        } catch (final IOException ioe) {
            throw new UnifiedSearchClientException("Unable to perform search by QueryBuilder", ioe);
        }
    }

    private RestHighLevelClient restHighLevelClient(final String indexName) {
        return indexName.equals(IndexInfo.CPS_CASE.getIndexName()) ? cpsCaseHighLevelClient : crimeCaseHighLevelClient;
    }
}
