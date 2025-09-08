package uk.gov.moj.cpp.system.documentgenerator.client;

import static java.lang.String.format;

import uk.gov.justice.services.common.rest.ServerPortProvider;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;

@SuppressWarnings("CdiInjectionPointsInspection")
@ApplicationScoped
public class DocumentGeneratorClientProducer {

    private static final String BASE_URI_TEMPLATE = "http://localhost:%s/systemdocgenerator-command-api/command/api/rest/systemdocgenerator";

    @Inject
    private ServerPortProvider serverPortProvider;

    @Inject
    private HttpClientFactory httpClientFactory;

    @Inject
    private SystemUserProvider systemUserProvider;

    @Produces
    public DocumentGeneratorClient documentGeneratorClient() {
        return new DefaultDocumentGeneratorClient(createBaseUri(), httpClientFactory, systemUserProvider);
    }

    private String createBaseUri() {
        return format(BASE_URI_TEMPLATE, serverPortProvider.getDefaultPort());
    }

}

