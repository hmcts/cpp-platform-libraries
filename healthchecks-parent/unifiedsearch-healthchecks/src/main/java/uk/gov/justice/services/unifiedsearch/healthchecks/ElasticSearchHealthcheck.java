package uk.gov.justice.services.unifiedsearch.healthchecks;

import static java.lang.String.format;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.cluster.health.ClusterHealthStatus.GREEN;
import static uk.gov.justice.services.healthcheck.api.HealthcheckResult.failure;
import static uk.gov.justice.services.healthcheck.api.HealthcheckResult.success;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;

import uk.gov.justice.services.healthcheck.api.Healthcheck;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

public class ElasticSearchHealthcheck implements Healthcheck {

    @Inject
    @Named(MONITOR_USER)
    private RestHighLevelClient restHighLevelClient;

    @Inject
    private ElasticSearchHealthQuerier elasticSearchHealthQuerier;

    @Override
    public String getHealthcheckName() {
        return "elastic-search-healthcheck";
    }

    @Override
    public String healthcheckDescription() {
        return "Verifies Elastic Search Health Status is '" + GREEN + "'";
    }

    @Override
    public HealthcheckResult runHealthcheck() {

        try {
            final ClusterHealthResponse clusterHealthResponse = elasticSearchHealthQuerier.getClusterHealth(
                    restHighLevelClient,
                    DEFAULT);

            final ClusterHealthStatus healthStatus = clusterHealthResponse.getStatus();

            if (healthStatus == GREEN) {
                return success();
            }

            return failure(format("Elastic Search healthcheck failed. CLuster Health Status should be '%s' but was '%s'", GREEN, healthStatus));

        } catch (final IOException e) {
            throw new ElasticSearchHealtcheckQueryException(format("IOException thrown when calling Elastic Search. Exception message: '%s'", e.getMessage()), e);
        }
    }
}
