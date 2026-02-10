package uk.gov.justice.services.unifiedsearch.client.search;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

@ApplicationScoped
class SearchResultConverter {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    private JacksonJsonpMapper jsonpMapper;

    @PostConstruct
    public void setup(){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        jsonpMapper = new JacksonJsonpMapper(objectMapper);
    }


    JsonArray toJsonArray(final List<Hit<JsonData>> searchHits, final Class<?> resultHitType) {
        final JsonArrayBuilder results = getJsonBuilderFactory().createArrayBuilder();
        searchHits.forEach(searchHit -> results.add(toJsonObject(searchHit, resultHitType)));
        return results.build();
    }

    JsonArray convertInnerHitsToJsonArray(final List<Hit<?>> searchHits,
                                         final Class<?> resultHitType,
                                         final String innerResultHightNodeName) {
        final JsonArrayBuilder results = getJsonBuilderFactory().createArrayBuilder();
        searchHits.forEach(searchHit -> toJsonObject(resultHitType, innerResultHightNodeName, results, searchHit));
        return results.build();
    }

    private void toJsonObject(Class<?> resultHitType, String innerResultHightNodeName, JsonArrayBuilder results, Hit<?> searchHit) {

        final Map<String, InnerHitsResult> innerHits = searchHit.innerHits();
        final InnerHitsResult resultsHits =  innerHits.get(innerResultHightNodeName);
        for (final Hit<JsonData> result : resultsHits.hits().hits()) {
            results.add(toJsonObject(result, resultHitType));
        }

    }

    private JsonObject toJsonObject(final Hit<JsonData> searchHit, Class<?> resultHitType) {
        try {
            Object pojo = searchHit.source()
                    .to(resultHitType, jsonpMapper);

            String json = objectMapper.writeValueAsString(pojo);

            try(final JsonReader reader = javax.json.Json.createReader(
                    new StringReader(json)
            )){
                return reader.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new UnifiedSearchClientException(
                    "Failed to deserialize search response", e);
        }
    }
}
