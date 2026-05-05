package uk.gov.moj.cpp.system.documentgenerator.client;

import uk.gov.justice.services.clients.core.webclient.WebTargetFactory;

import jakarta.ws.rs.client.Client;

public class HttpClientFactory {

    Client getClient(){
        return WebTargetFactory.getClient();
    }
}
