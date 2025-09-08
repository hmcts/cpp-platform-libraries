package uk.gov.moj.cpp.platform.data.utils.rest.service;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.net.MediaType.ZIP;
import static java.lang.Integer.parseInt;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import uk.gov.justice.services.common.configuration.Value;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RestClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientService.class);

    private static final Class DEFAULT_CLASS = byte[].class;
    private static final String CPPID = "CPPID";

    @Inject
    @Value(key = "restClient.httpConnection.poolSize", defaultValue = "10")
    private String httpConnectionPoolSize;

    @Inject
    @Value(key = "restClient.httpConnection.timeout", defaultValue = "120000")//2minutes
    private String httpConnectionTimeout;

    private ResteasyClient client;

    @PostConstruct
    public void init() {
        final int poolSize = parseInt(httpConnectionPoolSize);
        final int timeout = parseInt(httpConnectionTimeout);

        client = new ResteasyClientBuilderImpl()
                .connectionPoolSize(poolSize)
                .maxPooledPerRoute(poolSize)
                .connectionTTL(timeout, MILLISECONDS)
                .connectTimeout(timeout, MILLISECONDS)
                .connectionCheckoutTimeout(timeout, MILLISECONDS)
                .build();
    }

    @PreDestroy
    public void preDestroy() {
        client.close();
    }


    public Response get(final String url, final String fromDate, final String toDate) {
        return get(url, of("fromDate", fromDate, "toDate", toDate));
    }

    public Response get(final String url, final Map<String, String> params) {
        return get(url, of(ACCEPT, ZIP.toString()), params);
    }

    public Response get(final String url, final Map<String, String> headers, final Map<String, String> params) {
        final Invocation.Builder request = client.target(url)
                .queryParams(new MultivaluedHashMap<>(params))
                .request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.get();
    }

    public Response put(final String url, final Map<String, String> headers, final Object payload) {
        return put(url, headers, payload, Collections.emptyMap());
    }

    public Response put(final String url, final Map<String, String> headers, final Object payload, final Map<String, String> params) {
        final Invocation.Builder request = !params.isEmpty() ? client.target(url)
                .queryParams(new MultivaluedHashMap<>(params))
                .request() : client.target(url).request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.put(entity(payload, APPLICATION_JSON_TYPE));
    }

    public Response post(final String url, final Map<String, String> headers, final Object payload) {
        final Invocation.Builder request = client.target(url)
                .request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.post(entity(payload, APPLICATION_JSON_TYPE));
    }

    public Response delete(final String url, final Map<String, String> headers) {
        final Invocation.Builder request = client.target(url)
                .request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.delete();
    }

    public Response delete(final String url, final Map<String, String> headers, final Object payload, final Map<String, String> params) {
        final Invocation.Builder request = client.target(url)
                .queryParams(new MultivaluedHashMap<>(params))
                .request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.build(DELETE, entity(payload, APPLICATION_JSON_TYPE)).invoke(Response.class);
    }

    public Response delete(final String url, final Map<String, String> headers, final Object payload) {
        return delete(url, headers, payload, Collections.emptyMap());
    }

    public Response delete(final String url, final Map<String, String> headers, final Map<String, String> params) {
        final Invocation.Builder request = client.target(url)
                .queryParams(new MultivaluedHashMap<>(params))
                .request();

        if (headers != null && !headers.isEmpty()) {
            request.headers(new MultivaluedHashMap<>(headers));
        }

        return request.delete();
    }

    public Response newResponseFrom(final Response oldResponse) {
        return newResponseFrom(oldResponse, DEFAULT_CLASS);
    }

    public Response newResponseFrom(final Response oldResponse, final Class clazz) {
        try {
            final String cppId = oldResponse.getHeaderString(CPPID) == null ? randomUUID().toString() : oldResponse.getHeaderString(CPPID);
            final Response.ResponseBuilder responseBuilder = Response.status(oldResponse.getStatusInfo())
                    .type(oldResponse.getMediaType())
                    .header(CONTENT_DISPOSITION, oldResponse.getHeaderString(CONTENT_DISPOSITION))
                    .header(CPPID, cppId);

            if (oldResponse.getStatus() == HttpStatus.SC_OK) {
                return responseBuilder.entity(oldResponse.readEntity(clazz)).build();
            } else {
                return responseBuilder.entity(oldResponse.readEntity(DEFAULT_CLASS)).build();
            }
        } finally {
            try {
                oldResponse.close();
            } catch (RuntimeException e) {
                LOGGER.debug("Failed to close old response ", e);
            }
        }
    }
}
