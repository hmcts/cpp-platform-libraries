package uk.gov.moj.cpp.featurecontrol.remote;

import static java.time.ZonedDateTime.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.util.Clock;

import java.io.IOException;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AzureSignedRequestTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private AzureSignedRequest azureSignedRequest;

    @Test
    public void shouldCreateSignedHeaders() throws IOException {

        when(clock.now()).thenReturn(parse("2018-09-16T08:00Z"));

        final HttpUriRequest httpUriRequest = azureSignedRequest.createSignedRequest("http://localhost:8080", "myId", "mySecret");
        httpUriRequest.getAllHeaders();

        assertThat(httpUriRequest.getAllHeaders()[0].getName(), is("Authorization"));
        assertThat(httpUriRequest.getAllHeaders()[0].getValue(), is("HMAC-SHA256 Credential=myId, SignedHeaders=x-ms-date;host;x-ms-content-sha256, Signature=FOKelK6cFVO62/r7Jc+G07Z+VD7e6aOFdgQ1pNx8Pss="));

        assertThat(httpUriRequest.getAllHeaders()[1].getName(), is("x-ms-content-sha256"));
        assertThat(httpUriRequest.getAllHeaders()[1].getValue(), is("47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU="));

        assertThat(httpUriRequest.getAllHeaders()[2].getName(), is("x-ms-date"));
        assertThat(httpUriRequest.getAllHeaders()[2].getValue(), is("2018-09-16T08:00"));
    }
}
