package uk.gov.justice.services.unifiedsearch.healthchecks;

import java.io.IOException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;

/**
 * Wrapper class for the unmockable parts of getting a health response from elastic search
 */
public class ElasticSearchHealthQuerier {

    public HealthResponse getClusterHealth(
            final ElasticsearchClient client) throws IOException {

        return client.cluster().health();
    }
}
