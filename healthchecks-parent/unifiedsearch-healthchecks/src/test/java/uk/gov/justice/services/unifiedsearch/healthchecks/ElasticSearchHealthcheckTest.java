package uk.gov.justice.services.unifiedsearch.healthchecks;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.elasticsearch.cluster.health.ClusterHealthStatus.GREEN;
import static org.elasticsearch.cluster.health.ClusterHealthStatus.RED;
import static org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchHealthcheckTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private ElasticSearchHealthQuerier elasticSearchHealthQuerier;

    @InjectMocks
    private ElasticSearchHealthcheck elasticSearchHealthcheck;

    @Test
    public void shouldReturnCorrectHealthcheckName() throws Exception {

        assertThat(elasticSearchHealthcheck.getHealthcheckName(), is("elastic-search-healthcheck"));
    }

    @Test
    public void shouldReturnCorrectHealthcheckDescription() throws Exception {

        assertThat(elasticSearchHealthcheck.healthcheckDescription(), is("Verifies Elastic Search Health Status is 'GREEN'"));
    }

    @Test
    public void shouldReturnSuccessIfElasticSearchHealthStatusIsGreen() throws Exception {

        final ClusterHealthResponse clusterHealthResponse = mock(ClusterHealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient, DEFAULT)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.getStatus()).thenReturn(GREEN);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(true));
        assertThat(healthcheckResult.getErrorMessage(), is(empty()));
    }

    @Test
    public void shouldFailHealthcheckIfElasticSearchHealthStatusIsRed() throws Exception {

        final ClusterHealthResponse clusterHealthResponse = mock(ClusterHealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient, DEFAULT)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.getStatus()).thenReturn(RED);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(false));
        assertThat(healthcheckResult.getErrorMessage(), is(of("Elastic Search healthcheck failed. CLuster Health Status should be 'GREEN' but was 'RED'")));
    }

    @Test
    public void shouldFailHealthcheckIfElasticSearchHealthStatusIsYellow() throws Exception {

        final ClusterHealthResponse clusterHealthResponse = mock(ClusterHealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient, DEFAULT)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.getStatus()).thenReturn(YELLOW);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(false));
        assertThat(healthcheckResult.getErrorMessage(), is(of("Elastic Search healthcheck failed. CLuster Health Status should be 'GREEN' but was 'YELLOW'")));
    }

    @Test
    public void shouldFailIfQueryingElasticSearchThrowsIOException() throws Exception {

        final IOException ioException = new IOException("It all went wrong");

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient, DEFAULT)).thenThrow(ioException);

        final ElasticSearchHealtcheckQueryException elasticSearchHealtcheckQueryException = assertThrows(
                ElasticSearchHealtcheckQueryException.class,
                () -> elasticSearchHealthcheck.runHealthcheck());

        assertThat(elasticSearchHealtcheckQueryException.getMessage(), is("IOException thrown when calling Elastic Search. Exception message: 'It all went wrong'"));
        assertThat(elasticSearchHealtcheckQueryException.getCause(), is(ioException));
    }
}

