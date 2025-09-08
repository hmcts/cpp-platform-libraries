package uk.gov.moj.cpp.platform.data.utils.rest.service;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestClientServiceTest {

    public static final String HTTP_LOCALHOST = "http://localhost/";

    @Mock
    private ResteasyClient client;
    @Mock
    private ResteasyWebTarget target;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private Response response;

    @InjectMocks
    private RestClientService restClientService;

    @Test
    public void shouldPerformHttpGetRequestWithCorrectParams() {
        final String url = HTTP_LOCALHOST;
        final String fromDate = "2018-01-01";
        final String toDate = "2018-02-02";
        final MultivaluedHashMap<String, Object> paramsMap = new MultivaluedHashMap<>(ImmutableMap.of("fromDate", fromDate, "toDate", toDate));

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(paramsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.get()).thenReturn(response);

        restClientService.get(url, fromDate, toDate);

        verify(client).target(url);
        verify(target).queryParams(paramsMap);
    }

    @Test
    public void shouldPerformHttpGetRequestWithCorrectMapParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2");
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.get()).thenReturn(response);

        restClientService.get(url, params);

        verify(client).target(url);
        verify(target).queryParams(internalParamsMap);
    }

    @Test
    public void shouldReturnsResponseWithSameProperties() {
        final byte[] bytes = new byte[0];
        when(response.readEntity(byte[].class)).thenReturn(bytes);
        when(response.getStatusInfo()).thenReturn(NOT_FOUND);
        when(response.getMediaType()).thenReturn(MediaType.valueOf("application/zip"));
        when(response.getHeaderString("CPPID")).thenReturn(null);
        when(response.getHeaderString(CONTENT_DISPOSITION)).thenReturn("attachment; filename=\"application/zip\"");

        final Response newResponse = restClientService.newResponseFrom(response);

        assertThat(newResponse.getStatusInfo(), is(NOT_FOUND));
        assertThat(newResponse.getHeaderString(CONTENT_DISPOSITION), is("attachment; filename=\"application/zip\""));
        assertThat(newResponse.getEntity(), is(bytes));
        assertThat(newResponse.getHeaderString(CONTENT_TYPE), is("application/zip"));
        verify(response).close();
    }

    @Test
    public void shouldPerformHttpGetRequestWithHeaderAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(),
                "Ocp-Apim-Trace", "true");
        final Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2");
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);
        when(builder.get()).thenReturn(response);

        restClientService.get(url, headers, params);

        verify(client).target(url);
        verify(builder).headers(headersMap);
        verify(target).queryParams(internalParamsMap);
    }

    @Test
    public void shouldPerformPutRequestWithHeaderAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(),
                "Ocp-Apim-Trace", "true");

        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final String payload = "sample";

        when(client.target(url)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);

        restClientService.put(url, headers, payload);

        verify(client).target(url);
        verify(builder).headers(headersMap);
    }

    @Test
    public void shouldPerformHttpPutRequestWithHeaderAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(),
                "Ocp-Apim-Trace", "true");
        final Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2");
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);
        final String payload = "sample";

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);

        restClientService.put(url, headers, payload, params);

        verify(client).target(url);
        verify(builder).headers(headersMap);
        verify(target).queryParams(internalParamsMap);
    }

    @Test
    public void shouldPerformPostRequestWithHeaderAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(),
                "Ocp-Apim-Trace", "true");

        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final String payload = "sample";

        when(client.target(url)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);

        restClientService.post(url, headers, payload);

        verify(client).target(url);
        verify(builder).headers(headersMap);
    }

    @Test
    public void shouldReturnsResponseWithExpectedMediaType() {
        final JsonObject entity = createObjectBuilder().add("k1", "value").build();
        when(response.readEntity(JsonObject.class)).thenReturn(entity);
        when(response.getStatusInfo()).thenReturn(NOT_FOUND);
        when(response.getStatus()).thenReturn(HttpStatus.SC_OK);
        when(response.getMediaType()).thenReturn(MediaType.valueOf("application/json"));

        final Response newResponse = restClientService.newResponseFrom(response, JsonObject.class);

        assertThat(newResponse.getStatusInfo(), is(NOT_FOUND));
        assertThat(newResponse.getEntity(), is(entity));
        assertThat(newResponse.getHeaderString(CONTENT_TYPE), is("application/json"));
        verify(response).close();
    }

    @Test
    public void shouldPerformHttpDeleteRequestWithHeaderAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(), "Ocp-Apim-Trace", "true");
        final Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2");
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);
        when(builder.delete()).thenReturn(response);

        final Response actualResponse = restClientService.delete(url, headers, params);

        verify(client).target(url);
        verify(builder).headers(headersMap);
        verify(target).queryParams(internalParamsMap);
        verify(builder).delete();
        assertThat(actualResponse, is(response));
    }

    @Test
    public void shouldPerformHttpDeleteRequestWithHeaderAndEntityAndParams() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(), "Ocp-Apim-Trace", "true");
        final Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2");
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);
        final Invocation invocation = mock(Invocation.class);
        final String payload = "sample";

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);
        when(builder.build(eq(DELETE), any(Entity.class))).thenReturn(invocation);
        when(invocation.invoke(Response.class)).thenReturn(response);

        final Response actualResponse = restClientService.delete(url, headers, payload, params);

        verify(client).target(url);
        verify(target).queryParams(internalParamsMap);
        verify(target).request();
        verify(builder).headers(headersMap);
        verify(builder).build(eq(DELETE), any(Entity.class));
        verify(invocation).invoke(Response.class);
        assertThat(actualResponse, is(response));
    }

    @Test
    public void shouldPerformHttpDeleteRequestWithHeaderAndEntity() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(), "Ocp-Apim-Trace", "true");
        final Map<String, String> params = ImmutableMap.of();
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);
        final MultivaluedHashMap<String, Object> internalParamsMap = new MultivaluedHashMap<>(params);
        final Invocation invocation = mock(Invocation.class);
        final String payload = "sample";

        when(client.target(url)).thenReturn(target);
        when(target.queryParams(internalParamsMap)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);
        when(builder.build(eq(DELETE), any(Entity.class))).thenReturn(invocation);
        when(invocation.invoke(Response.class)).thenReturn(response);

        final Response actualResponse = restClientService.delete(url, headers, payload);

        verify(client).target(url);
        verify(target).queryParams(internalParamsMap);
        verify(target).request();
        verify(builder).headers(headersMap);
        verify(builder).build(eq(DELETE), any(Entity.class));
        verify(invocation).invoke(Response.class);
        assertThat(actualResponse, is(response));
    }

    @Test
    public void shouldPerformHttpDeleteRequestWithHeader() {
        final String url = HTTP_LOCALHOST;
        final Map<String, String> headers = ImmutableMap.of("Ocp-Apim-Subscription-Key", "dummy-subscription-key",
                HttpHeaders.ACCEPT, JSON_UTF_8.toString(), "Ocp-Apim-Trace", "true");
        final MultivaluedHashMap<String, Object> headersMap = new MultivaluedHashMap<>(headers);

        when(client.target(url)).thenReturn(target);
        when(target.request()).thenReturn(builder);
        when(builder.headers(headersMap)).thenReturn(builder);
        when(builder.delete()).thenReturn(response);

        final Response actualResponse = restClientService.delete(url, headers);

        verify(client).target(url);
        verify(builder).headers(headersMap);
        verify(builder).delete();
        assertThat(actualResponse, is(response));
    }
}