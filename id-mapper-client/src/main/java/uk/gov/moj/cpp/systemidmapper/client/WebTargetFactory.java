package uk.gov.moj.cpp.systemidmapper.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.Closeable;

import static javax.ws.rs.client.ClientBuilder.newClient;

public class WebTargetFactory implements Closeable {

    private final Client client;
    private final String baseUri;
    private final String path;

    public WebTargetFactory(final String baseUri, final String path) {
        this.baseUri = baseUri;
        this.path = path;
        this.client = newClient();
    }

    public WebTarget build() {
        return client.target(baseUri).path(path);
    }

    @Override
    public void close() {
        this.client.close();
    }
}
