package uk.gov.moj.cpp.platform.azure.utils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import liquibase.pro.packaged.A;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class APIMServiceTest {

    @Mock
    private RestEasyClientService restEasyClientService;

    @InjectMocks
    private APIMService apimService;

    @Mock
    private Response response;

    @Test
    public void shouldTestApimServiceTestWithSuccessResponse() throws InterruptedException {
        response = Response.status(Response.Status.OK).build();

        when(restEasyClientService.post(anyString(),anyString(),anyString())).thenReturn(response);

        apimService.triggerAPIMServiceWithRetryOnFailures("requestPayload", new int[]{1, 5, 10}, 3,60, "http://localhost:8080/sdrsApi","1674a16507104b749a76b19b6c837351");

    }

    @Test
    public void shouldTestApimServiceTestWithErrorResponse() throws InterruptedException {
        response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        when(restEasyClientService.post(anyString(),anyString(),anyString())).thenReturn(response);

        apimService.triggerAPIMServiceWithRetryOnFailures("requestPayload", new int[]{1, 2}, 2,10, "http://localhost:8080/sdrsApi","dummy-subscription-key");

    }

}
