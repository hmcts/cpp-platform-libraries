package uk.gov.moj.cpp.platform.azure.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.status;
import static org.mockito.Mockito.when;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class RestEasyClientServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ResteasyWebTarget targetMock;
    @InjectMocks
    private RestEasyClientService restEasyClientService;
    @Mock
    private Invocation.Builder mockedRequest;
    @Mock
    private Response response;

    @Test
    public void shouldTestForResponseForPostingRequest() {
        response = status(OK).type(APPLICATION_JSON).entity(successJsonSummary()).build();
        when(targetMock.request()).thenReturn(mockedRequest);

    }

    private JsonObject successJsonSummary() {
        return Json.createObjectBuilder()
                .add("Status", "SUCCESS")
                .build();
    }

}
