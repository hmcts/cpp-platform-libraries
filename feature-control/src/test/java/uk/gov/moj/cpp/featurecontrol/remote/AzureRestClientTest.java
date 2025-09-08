package uk.gov.moj.cpp.featurecontrol.remote;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.json.JsonObject;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AzureRestClientTest {

    private static final String URL = "localhost";
    private static final String ID = "myId";
    private static final String SECRET = "mySecret";

    @InjectMocks
    private AzureRestClient azureRestClient;

    @Mock
    private AzureSignedRequest azureSignedRequest;

    @Mock
    private MockClosableHttpClient httpClient;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    @Captor
    private ArgumentCaptor<String> secretCaptor;

    @Test
    public void shouldCallAzureWithASignedRequestWhenHttpStatusIsOk() throws IOException {

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(response.getEntity()).thenReturn(new StringEntity("{\"key\":\"myFeature\"}"));
        when(azureSignedRequest.createSignedRequest(any(), any(), any())).thenReturn(new HttpGet());
        when(httpClient.execute(any())).thenReturn(response);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.getStatusLine()).thenReturn(statusLine);

        final JsonObject jsonObject = azureRestClient.executeGet(URL, ID, SECRET);

        verify(azureSignedRequest).createSignedRequest(eq("localhost"),
                eq("myId"),
                eq("mySecret"));

        assertThat(jsonObject.getString("key"), is("myFeature"));
    }

    @Test
    public void shouldCallAzureWithASignedRequestReturnEmptyResultWhenHttpStatusIsOk() throws IOException {

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(response.getEntity()).thenReturn(new StringEntity(""));
        when(azureSignedRequest.createSignedRequest(any(), any(), any())).thenReturn(new HttpGet());
        when(httpClient.execute(any())).thenReturn(response);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.getStatusLine()).thenReturn(statusLine);

        final JsonObject jsonObject = azureRestClient.executeGet(URL, ID, SECRET);

        verify(azureSignedRequest).createSignedRequest(eq("localhost"),
                eq("myId"),
                eq("mySecret"));

        assertThat(jsonObject.size(), is(0));
    }

    @Test
    public void shouldReturnEmptyObjectWhenHttpStatusIsForbidden() throws IOException {

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(azureSignedRequest.createSignedRequest(any(), any(), any())).thenReturn(new HttpGet());
        when(httpClient.execute(any())).thenReturn(response);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_FORBIDDEN);
        when(response.getStatusLine()).thenReturn(statusLine);

        final JsonObject jsonObject = azureRestClient.executeGet(URL, ID, SECRET);

        assertThat(jsonObject.size(), is(0));
    }


    @Test
    public void shouldReturnAzureRequestException() throws IOException {

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(azureSignedRequest.createSignedRequest(any(), any(), any())).thenReturn(new HttpGet());
        when(httpClient.execute(any())).thenThrow(IOException.class);

        final AzureRequestException azureRequestException = assertThrows(AzureRequestException.class, () -> {
            azureRestClient.executeGet(URL, ID, SECRET);
        });

        assertThat(azureRequestException.getMessage(), is(format("Failed to getting feature from azure; url %s", URL)));
    }
}
