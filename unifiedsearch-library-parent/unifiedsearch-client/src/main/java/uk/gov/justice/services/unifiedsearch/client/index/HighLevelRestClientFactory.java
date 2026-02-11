package uk.gov.justice.services.unifiedsearch.client.index;

import static org.apache.http.HttpHost.create;
import static org.apache.http.impl.nio.reactor.IOReactorConfig.custom;
import static org.elasticsearch.client.RestClient.builder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;


@ApplicationScoped
public class HighLevelRestClientFactory {

    private ObjectMapper objectMapper;

    @PostConstruct
    public void setUp(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ElasticsearchClient createNew(
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

        RestClient restClient = restClientBuilder.build();

        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);

        return new ElasticsearchClient(transport);
    }
}
