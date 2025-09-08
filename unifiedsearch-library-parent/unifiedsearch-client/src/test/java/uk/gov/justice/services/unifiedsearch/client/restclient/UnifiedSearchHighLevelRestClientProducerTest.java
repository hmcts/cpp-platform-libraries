package uk.gov.justice.services.unifiedsearch.client.restclient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

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

        final RestHighLevelClient readHighLevelClient = mock(RestHighLevelClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(READ_USER)).thenReturn(readHighLevelClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getReadHighLevelClient(), is(readHighLevelClient));
    }

    @Test
    public void shouldCreateHighLevelRestClientForTheWriteUser() throws Exception {

        final RestHighLevelClient writeHighLevelClient = mock(RestHighLevelClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(WRITE_USER)).thenReturn(writeHighLevelClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getWriteHighLevelClient(), is(writeHighLevelClient));
    }

    @Test
    public void shouldCreateHighLevelRestClientForTheMonitorUser() throws Exception {

        final RestHighLevelClient monitorHighLevelClient = mock(RestHighLevelClient.class);

        when(highLevelRestClientProvider.newHighLevelClientFor(MONITOR_USER)).thenReturn(monitorHighLevelClient);

        assertThat(unifiedSearchHighLevelRestClientProducer.getMonitorHighLevelClient(), is(monitorHighLevelClient));
    }
}
