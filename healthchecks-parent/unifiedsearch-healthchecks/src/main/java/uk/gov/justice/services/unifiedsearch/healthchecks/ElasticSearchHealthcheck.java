package uk.gov.justice.services.unifiedsearch.healthchecks;

import static co.elastic.clients.elasticsearch._types.HealthStatus.Green;
import static java.lang.String.format;
import static uk.gov.justice.services.healthcheck.api.HealthcheckResult.failure;
import static uk.gov.justice.services.healthcheck.api.HealthcheckResult.success;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;

import uk.gov.justice.services.healthcheck.api.Healthcheck;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.HealthStatus;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;


public class ElasticSearchHealthcheck implements Healthcheck {

    @Inject
    @Named(MONITOR_USER)
    private ElasticsearchClient restHighLevelClient;

    @Inject
    private ElasticSearchHealthQuerier elasticSearchHealthQuerier;

    @Override
    public String getHealthcheckName() {
        return "elastic-search-healthcheck";
    }

    @Override
    public String healthcheckDescription() {
        return "Verifies Elastic Search Health Status is '" + Green + "'";
    }

    @Override
    public HealthcheckResult runHealthcheck() {

        try {
            final HealthResponse clusterHealthResponse = elasticSearchHealthQuerier.getClusterHealth(
                    restHighLevelClient);

            final HealthStatus healthStatus = clusterHealthResponse.status();

            if (healthStatus == Green) {
                return success();
            }

            return failure(format("Elastic Search healthcheck failed. CLuster Health Status should be '%s' but was '%s'", Green, healthStatus));

        } catch (final IOException e) {
            throw new ElasticSearchHealtcheckQueryException(format("IOException thrown when calling Elastic Search. Exception message: '%s'", e.getMessage()), e);
        }
    }
}
