package uk.gov.justice.services.unifiedsearch.client.restclient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UnifiedSearchHighLevelRestClientProducerTest {

    @Mock
    private HighLevelRestClientProvider highLevelRestClientProvider;

    @InjectMocks
    private UnifiedSearchHighLevelRestClientProducer unifiedSearchHighLevelRestClientProducer;

    @Test
    public void shouldCreateHighLevelRestClientForTheReadUser() throws Exception {

        final ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(READ_USER)).thenReturn(elasticsearchClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getReadHighLevelClient(), is(elasticsearchClient));
    }

    @Test
    public void shouldCreateHighLevelRestClientForTheWriteUser() throws Exception {

        final ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(WRITE_USER)).thenReturn(elasticsearchClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getWriteHighLevelClient(), is(elasticsearchClient));
    }

    @Test
    public void shouldCreateHighLevelRestClientForTheMonitorUser() throws Exception {

        final ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(MONITOR_USER)).thenReturn(elasticsearchClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getMonitorHighLevelClient(), is(elasticsearchClient));
    }
}
