package uk.gov.moj.cpp.system.documentgenerator.client;

import static javax.ws.rs.client.ClientBuilder.newClient;

import javax.ws.rs.client.Client;

public class HttpClientFactory {

    Client getClient(){
        return newClient();
    }
}
