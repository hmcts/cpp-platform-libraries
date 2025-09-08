package uk.gov.justice.services.unifiedsearch.client.search;

import static javax.json.Json.createArrayBuilder;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

@ApplicationScoped
class SearchResultConverter {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    JsonArray toJsonArray(final SearchHits searchHits, final Class<?> resultHitType) {
        final JsonArrayBuilder results = createArrayBuilder();
        searchHits.forEach(searchHit -> results.add(toJsonObject(searchHit, resultHitType)));
        return results.build();
    }

    JsonArray convertInnerHitsToJsonArray(final SearchHits searchHits,
                                         final Class<?> resultHitType,
                                         final String innerResultHightNodeName) {
        final JsonArrayBuilder results = createArrayBuilder();
        searchHits.forEach(searchHit -> toJsonObject(resultHitType, innerResultHightNodeName, results, searchHit));
        return results.build();
    }

    private void toJsonObject(Class<?> resultHitType, String innerResultHightNodeName, JsonArrayBuilder results, SearchHit searchHit) {
        try {
            final Map<String, SearchHits> innerHits = searchHit.getInnerHits();
            final SearchHits resultsHits =  innerHits.get(innerResultHightNodeName);
            for (final SearchHit result : resultsHits) {
                final Object innerHitValue = objectMapper.readValue(result.getSourceAsString(), resultHitType);
                results.add(objectToJsonObjectConverter.convert(innerHitValue));
            }
        } catch (final IOException e) {
            throw new UnifiedSearchClientException("Failed to deserialize search response", e);
        }
    }

    private JsonObject toJsonObject(final SearchHit searchHit, Class<?> resultHitType) {
        try {
            /*Using objectMapper so that we selectively map only properties/hierarchy
            defined in resultHitType are populated*/
            final Object hitValue = objectMapper.readValue(searchHit.getSourceAsString(), resultHitType);

            return objectToJsonObjectConverter.convert(hitValue);

        } catch (final IOException e) {
            throw new UnifiedSearchClientException("Failed to deserialize search response", e);
        }
    }
}
