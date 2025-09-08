package uk.gov.justice.services.unifiedsearch.client.index;

import static org.apache.http.HttpHost.create;
import static org.apache.http.impl.nio.reactor.IOReactorConfig.custom;
import static org.elasticsearch.client.RestClient.builder;

import javax.enterprise.context.ApplicationScoped;

import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

@ApplicationScoped
public class HighLevelRestClientFactory {

    public RestHighLevelClient createNew(
            final String elasticsearchBaseUri,
            final CredentialsProvider credentialsProvider,
            final int elasticsearchTimeout,
            final int threadCount) {


        final RestClientBuilder restClientBuilder = builder(create(elasticsearchBaseUri))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setDefaultIOReactorConfig(custom()
                                .setIoThreadCount(threadCount)
                                .build()))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(elasticsearchTimeout)
                                .setSocketTimeout(elasticsearchTimeout)
                                .setConnectionRequestTimeout(elasticsearchTimeout));

        return new RestHighLevelClient(restClientBuilder);
    }
}
