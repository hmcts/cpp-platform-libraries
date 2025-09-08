package uk.gov.justice.services.unifiedsearch.client.index;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HighLevelRestClientFactoryTest {

    @InjectMocks
    private HighLevelRestClientFactory highLevelRestClientFactory;

    @Test
    public void shouldCreateNewRestClient() throws Exception {

        final String elasticsearchBaseUri = "http://localhost:9200";
        final int elasticsearchTimeout = 5000;
        final int threadCount = 100;

        final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);

        final RestHighLevelClient restHighLevelClient = highLevelRestClientFactory.createNew(
                elasticsearchBaseUri,
                credentialsProvider,
                elasticsearchTimeout,
                threadCount);

        assertThat(restHighLevelClient, is(notNullValue()));
    }
}
