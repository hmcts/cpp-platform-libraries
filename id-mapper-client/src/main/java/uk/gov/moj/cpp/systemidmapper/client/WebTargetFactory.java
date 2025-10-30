package uk.gov.moj.cpp.systemidmapper.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class WebTargetFactory {

    private final Client client;
    private final String baseUri;
    private final String path;

    public WebTargetFactory(final String baseUri, final String path) {
        this.baseUri = baseUri;
        this.path = path;
        this.client = uk.gov.justice.services.clients.core.webclient.WebTargetFactory.getClient();
    }

    public WebTarget build() {
        return client.target(baseUri).path(path);
    }
}
