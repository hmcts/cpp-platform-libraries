package uk.gov.moj.cpp.featurecontrol.remote;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;

public class AzureRestClient {
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Inject
    private AzureSignedRequest azureSignedRequest;

    public JsonObject executeGet(String url, String id, String secret) {

        HttpUriRequest request = azureSignedRequest.createSignedRequest(url, id, secret);
        try (final CloseableHttpResponse closeableHttpResponse = httpClient.execute(request)) {

            if (getStatusCode(closeableHttpResponse) != HttpStatus.SC_OK) {
                return getJsonBuilderFactory().createObjectBuilder().build();
            }

            String result = EntityUtils.toString(closeableHttpResponse.getEntity());
            if (isEmpty(result)) {
                return getJsonBuilderFactory().createObjectBuilder().build();
            } else {
                try (JsonReader reader = getJsonReaderFactory().createReader(new StringReader(result))) {
                    return reader.readObject();
                }
            }
        } catch (IOException e) {
            throw new AzureRequestException(format("Failed to getting feature from azure; url %s", url), e);
        }
    }

    private int getStatusCode(final CloseableHttpResponse closeableHttpResponse) {
        return closeableHttpResponse.getStatusLine().getStatusCode();
    }

}
