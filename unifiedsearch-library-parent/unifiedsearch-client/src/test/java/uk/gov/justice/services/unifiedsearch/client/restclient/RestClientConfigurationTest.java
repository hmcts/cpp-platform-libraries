package uk.gov.justice.services.unifiedsearch.client.restclient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class RestClientConfigurationTest {

    @InjectMocks
    private RestClientConfiguration restClientConfiguration;

    @Test
    public void shouldGetRestClientConfigurationThatWouldBeSetByJndi() throws Exception {

        final String elasticsearchBaseUri = "http://localhost:9200";
        final String elasticsearchTimeout = "5000";
        final String restClientThreadCount = "200";

        setField(restClientConfiguration, "elasticsearchBaseUri", elasticsearchBaseUri);
        setField(restClientConfiguration, "elasticsearchTimeout", elasticsearchTimeout);
        setField(restClientConfiguration, "restClientThreadCount", restClientThreadCount);

        assertThat(restClientConfiguration.getElasticsearchBaseUri(), is(elasticsearchBaseUri));
        assertThat(restClientConfiguration.getElasticsearchTimeout(), is(5000));
        assertThat(restClientConfiguration.getRestClientThreadCount(), is(200));
    }
}
