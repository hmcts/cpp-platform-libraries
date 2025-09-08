package uk.gov.justice.services.unifiedsearch.client.restclient;

import uk.gov.justice.services.unifiedsearch.client.index.HighLevelRestClientFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestHighLevelClient;

@ApplicationScoped
public class HighLevelRestClientProvider {

    @Inject
    private RestClientConfiguration restClientConfiguration;

    @Inject
    private UnifiedSearchCredentialsProvider unifiedSearchCredentialsProvider;

    @Inject
    private HighLevelRestClientFactory highLevelRestClientFactory;

    public RestHighLevelClient newHighLevelClientFor(final String userType) {

        final String elasticsearchBaseUri = restClientConfiguration.getElasticsearchBaseUri();
        final int elasticsearchTimeout = restClientConfiguration.getElasticsearchTimeout();
        final int restClientThreadCount = restClientConfiguration.getRestClientThreadCount();

        final CredentialsProvider credentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(userType);

        return highLevelRestClientFactory.createNew(
                elasticsearchBaseUri,
                credentialsProvider,
                elasticsearchTimeout,
                restClientThreadCount);
    }


}
