package uk.gov.moj.cpp.systemidmapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.common.rest.ServerPortProvider;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 21787)
public class SystemIdMapperClientProducerTest {

    private static final int PORT = 21787;
    private static final String MAPPINGS_URL = "/system-id-mapper-api/rest/systemid/mappings";

    @Mock
    private ServerPortProvider defaultServerPortProvider;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();

    @InjectMocks
    private SystemIdMapperClientProducer producer;

    @Test
    public void shouldCreateInstanceOfSystemIdMapperClient() throws Exception {
        when(defaultServerPortProvider.getDefaultPort()).thenReturn("8080");

        assertThat(producer.systemIdMapperClient(), instanceOf(SystemIdMapperClient.class));
    }

    @Test
    public void shouldUsePortProvidedByDefaultServerPortProvider() throws Exception {

        when(defaultServerPortProvider.getDefaultPort()).thenReturn(String.valueOf(PORT));

        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody("{ \"id\": \"266c0ae9-e276-4d29-b669-cb32013228b4\",\n}")));

        final String sourceId = "sourceIdAbc";
        final String sourceType = "sourceIdTypeBCD";
        final UUID targetId = randomUUID();
        final String targetType = "targetIdTypeEFG";
        final UUID userId = randomUUID();

        final SystemIdMap systemIdMap = new SystemIdMap(sourceId, sourceType, targetId, targetType);
        producer.systemIdMapperClient().add(systemIdMap, userId);

        verify(postRequestedFor(urlPathEqualTo(MAPPINGS_URL))
                .withRequestBody(equalToJson(mapJsonOf(sourceId, sourceType, targetId, targetType))));

    }

    private String mapJsonOf(final String sourceId, final String sourceType, final UUID targetId, final String targetType) {
        return "{\n" +
                "  \"sourceId\": \"" + sourceId + "\",\n" +
                "  \"sourceType\": \"" + sourceType + "\",\n" +
                "  \"targetId\":\"" + targetId.toString() + "\",\n" +
                "  \"targetType\":\"" + targetType + "\"\n" +
                "}";
    }
}
