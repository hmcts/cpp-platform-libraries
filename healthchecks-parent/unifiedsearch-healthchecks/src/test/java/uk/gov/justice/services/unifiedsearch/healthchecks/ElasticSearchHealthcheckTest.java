package uk.gov.justice.services.unifiedsearch.healthchecks;

import static co.elastic.clients.elasticsearch._types.HealthStatus.Green;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.HealthStatus;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchHealthcheckTest {

    @Mock
    private ElasticsearchClient restHighLevelClient;

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

        assertThat(elasticSearchHealthcheck.healthcheckDescription(), is("Verifies Elastic Search Health Status is 'Green'"));
    }

    @Test
    public void shouldReturnSuccessIfElasticSearchHealthStatusIsGreen() throws Exception {

        final HealthResponse clusterHealthResponse = mock(HealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.status()).thenReturn(Green);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(true));
        assertThat(healthcheckResult.getErrorMessage(), is(empty()));
    }

    @Test
    public void shouldFailHealthcheckIfElasticSearchHealthStatusIsRed() throws Exception {

        final HealthResponse clusterHealthResponse = mock(HealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.status()).thenReturn(HealthStatus.Red);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(false));
        assertThat(healthcheckResult.getErrorMessage(), is(of("Elastic Search healthcheck failed. CLuster Health Status should be 'Green' but was 'Red'")));
    }

    @Test
    public void shouldFailHealthcheckIfElasticSearchHealthStatusIsYellow() throws Exception {

        final HealthResponse clusterHealthResponse = mock(HealthResponse.class);

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient)).thenReturn(clusterHealthResponse);
        when(clusterHealthResponse.status()).thenReturn(HealthStatus.Yellow);

        final HealthcheckResult healthcheckResult = elasticSearchHealthcheck.runHealthcheck();
        assertThat(healthcheckResult.isPassed(), is(false));
        assertThat(healthcheckResult.getErrorMessage(), is(of("Elastic Search healthcheck failed. CLuster Health Status should be 'Green' but was 'Yellow'")));
    }

    @Test
    public void shouldFailIfQueryingElasticSearchThrowsIOException() throws Exception {

        final IOException ioException = new IOException("It all went wrong");

        when(elasticSearchHealthQuerier.getClusterHealth(restHighLevelClient)).thenThrow(ioException);

        final ElasticSearchHealtcheckQueryException elasticSearchHealtcheckQueryException = assertThrows(
                ElasticSearchHealtcheckQueryException.class,
                () -> elasticSearchHealthcheck.runHealthcheck());

        assertThat(elasticSearchHealtcheckQueryException.getMessage(), is("IOException thrown when calling Elastic Search. Exception message: 'It all went wrong'"));
        assertThat(elasticSearchHealtcheckQueryException.getCause(), is(ioException));
    }
}

