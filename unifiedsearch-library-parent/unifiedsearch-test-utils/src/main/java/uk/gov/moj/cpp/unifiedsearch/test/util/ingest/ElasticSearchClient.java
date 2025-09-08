package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.elasticsearch.client.RestClient.builder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.CrimeIndexConstants.ES_URI;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClient {

    private static final CredentialProviderUtil credentialProvider = new CredentialProviderUtil();

    public RestHighLevelClient restClient() {

        return restClient(CRIME_CASE);
    }

    public RestHighLevelClient adminRestClient() {

        return adminRestClient(CRIME_CASE);
    }

    public RestHighLevelClient restClient(final IndexInfo indexInfo) {

        final RestClientBuilder restClientBuilder = builder(HttpHost.create(ES_URI));

        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialProvider.credentialsProvider(indexInfo)));

        return new RestHighLevelClient(restClientBuilder);
    }

    public RestHighLevelClient adminRestClient(final IndexInfo indexInfo) {

        final RestClientBuilder restClientBuilder = builder(HttpHost.create(ES_URI));

        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialProvider.adminCredentialsProvider(indexInfo)));

        return new RestHighLevelClient(restClientBuilder);
    }
}
