package uk.gov.justice.services.unifiedsearch.client.restclient;

import static java.lang.Integer.parseInt;

import uk.gov.justice.services.common.configuration.GlobalValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RestClientConfiguration {

    @Inject
    @GlobalValue(key = "elasticsearchBaseUri", defaultValue = "http://localhost:9200")
    private String elasticsearchBaseUri;

    @Inject
    @GlobalValue(key = "elasticsearchTimeout", defaultValue = "5000")
    private String elasticsearchTimeout;

    @Inject
    @GlobalValue(key = "restClientThreadCount", defaultValue = "200")
    private String restClientThreadCount;

    public String getElasticsearchBaseUri() {
        return elasticsearchBaseUri;
    }

    public int getElasticsearchTimeout() {
        return parseInt(elasticsearchTimeout);
    }

    public int getRestClientThreadCount() {
        return parseInt(restClientThreadCount);
    }
}
