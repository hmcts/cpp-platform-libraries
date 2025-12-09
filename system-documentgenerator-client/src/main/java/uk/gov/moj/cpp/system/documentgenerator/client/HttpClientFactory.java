package uk.gov.moj.cpp.system.documentgenerator.client;

import uk.gov.justice.services.clients.core.webclient.WebTargetFactory;

import javax.ws.rs.client.Client;

public class HttpClientFactory {

    Client getClient(){
        return WebTargetFactory.getClient();
    }
}
