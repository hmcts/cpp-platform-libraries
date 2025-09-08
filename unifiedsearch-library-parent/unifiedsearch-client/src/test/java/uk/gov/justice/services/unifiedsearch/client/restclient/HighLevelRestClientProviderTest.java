package uk.gov.justice.services.unifiedsearch.client.restclient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.index.HighLevelRestClientFactory;

import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class HighLevelRestClientProviderTest {

    @Mock
    private RestClientConfiguration restClientConfiguration;

    @Mock
    private UnifiedSearchCredentialsProvider unifiedSearchCredentialsProvider;

    @Mock
    private HighLevelRestClientFactory highLevelRestClientFactory;

    @InjectMocks
    private HighLevelRestClientProvider highLevelRestClientProvider;

    @Test
    public void shouldCreateARestClientWithTheCorrectConfiguration() throws Exception {

        final String elasticsearchBaseUri = "elasticsearchBaseUri";
        final int elasticsearchTimeout = 2384;
        final String userType = WRITE_USER;
        final int threadCount = 23;

        final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
        final RestHighLevelClient restHighLevelClient = mock(RestHighLevelClient.class);

        when(restClientConfiguration.getElasticsearchBaseUri()).thenReturn(elasticsearchBaseUri);
        when(restClientConfiguration.getElasticsearchTimeout()).thenReturn(elasticsearchTimeout);
        when(restClientConfiguration.getRestClientThreadCount()).thenReturn(threadCount);
        when(unifiedSearchCredentialsProvider.getCredentialsProvider(userType)).thenReturn(credentialsProvider);

        when(highLevelRestClientFactory.createNew(
                elasticsearchBaseUri,
                credentialsProvider,
                elasticsearchTimeout,
                threadCount)).thenReturn(restHighLevelClient);

        assertThat(highLevelRestClientProvider.newHighLevelClientFor(userType), is(restHighLevelClient));
    }
}
