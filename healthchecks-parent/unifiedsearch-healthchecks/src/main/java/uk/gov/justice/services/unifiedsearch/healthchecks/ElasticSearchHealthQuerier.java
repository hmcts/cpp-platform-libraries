package uk.gov.justice.services.unifiedsearch.healthchecks;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Wrapper class for the unmockable parts of getting a health response from elastic search
 */
public class ElasticSearchHealthQuerier {

    public ClusterHealthResponse getClusterHealth(
            final RestHighLevelClient restHighLevelClient,
            final RequestOptions requestOptions) throws IOException {

        return restHighLevelClient
                .cluster()
                .health(new ClusterHealthRequest(), requestOptions);
    }
}
