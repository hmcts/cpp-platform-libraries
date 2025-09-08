package uk.gov.moj.cpp.systemidmapper.client;

import static java.lang.String.format;

import uk.gov.justice.services.common.rest.ServerPortProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class SystemIdMapperClientProducer {

    private static final String BASE_URI_TEMPLATE = "http://localhost:%s/system-id-mapper-api/rest/systemid/";

    @Inject
    ServerPortProvider serverPortProvider;

    @Inject
    ObjectMapper objectMapper;

    @Produces
    public SystemIdMapperClient systemIdMapperClient() {
        return new DefaultSystemIdMapperClient(createBaseUri(), objectMapper);
    }

    private String createBaseUri() {
        return format(BASE_URI_TEMPLATE, serverPortProvider.getDefaultPort());
    }
}
