package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.elasticsearch.client.RestClient.builder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.CrimeIndexConstants.ES_URI;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClientBuilder;

public class ElasticSearchClient {

    private static final CredentialProviderUtil credentialProvider = new CredentialProviderUtil();

    public ElasticsearchClient restClient() {

        return restClient(CRIME_CASE);
    }

    public ElasticsearchClient adminRestClient() {

        return adminRestClient(CRIME_CASE);
    }

    public ElasticsearchClient restClient(final IndexInfo indexInfo) {

        final RestClientBuilder restClientBuilder = builder(HttpHost.create(ES_URI));

        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialProvider.credentialsProvider(indexInfo)));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        RestClientTransport transport = new RestClientTransport(restClientBuilder.build(), jsonpMapper);

        return new ElasticsearchClient(transport);
    }

    public ElasticsearchClient adminRestClient(final IndexInfo indexInfo) {

        final RestClientBuilder restClientBuilder = builder(HttpHost.create(ES_URI));

        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialProvider.adminCredentialsProvider(indexInfo)));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        RestClientTransport transport = new RestClientTransport(restClientBuilder.build(), jsonpMapper);

        return new ElasticsearchClient(transport);
    }
}
