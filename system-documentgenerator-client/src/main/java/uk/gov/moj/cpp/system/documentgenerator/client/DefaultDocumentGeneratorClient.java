package uk.gov.moj.cpp.system.documentgenerator.client;

import org.apache.commons.io.IOUtils;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static uk.gov.justice.services.common.http.HeaderConstants.USER_ID;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

@SuppressWarnings("WeakerAccess")
public class DefaultDocumentGeneratorClient implements DocumentGeneratorClient {

    private static final String DOCUMENT_CONVERSION_FORMAT_PDF = "pdf";
    private static final String DOCUMENT_CONVERSION_FORMAT_DOCX = "docx";
    private static final String RENDER_REQUEST_TYPE = "application/vnd.systemdocgenerator.render+json";
    private static final String THYMELEAF_REQUEST_TYPE = "application/vnd.systemdocgenerator.thymeleaf.render+json";
    private static final String URL_ENDS_WITH = "/render";

    private static final String KEY_TEMPLATE_NAME = "templateName";
    private static final String KEY_CONVERSION_FORMAT = "conversionFormat";
    private static final String KEY_TEMPLATE_PAYLOAD = "templatePayload";

    private final String baseUri;
    private final HttpClientFactory httpClientFactory;

    private final SystemUserProvider systemUserProvider;

    public DefaultDocumentGeneratorClient(final String baseUri, final HttpClientFactory httpClientFactory, final SystemUserProvider systemUserProvider) {
        this.baseUri = baseUri;
        this.httpClientFactory = httpClientFactory;
        this.systemUserProvider = systemUserProvider;

    }

    public byte[] generatePdfDocument(final JsonObject jsonData, final String templateIdentifier, final UUID userId) throws IOException {
        Response response = null;

        try {
            Client client = httpClientFactory.getClient();
            response = callPDFService(client, jsonData, templateIdentifier, userId);

            final Status status = fromStatusCode(response.getStatus());
            if (OK.equals(status)) {
                final InputStream is = response.readEntity(InputStream.class);
                return IOUtils.toByteArray(is);
            }
            throw new DocumentGeneratorClientException(format("Failed to generate document with identifier %s. Http status: %s, Http message: %s", templateIdentifier, status.getStatusCode(), status.getReasonPhrase()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public byte[] generateThymeleafDocument(final JsonObject jsonData, final String templateIdentifier) throws IOException {
        Response response = null;
        try {
            Client client = httpClientFactory.getClient();

            response = callThymeleafService(client, jsonData, templateIdentifier);

            final Status status = fromStatusCode(response.getStatus());
            if (OK.equals(status)) {
                final InputStream is = response.readEntity(InputStream.class);
                return IOUtils.toByteArray(is);
            }
            throw new DocumentGeneratorClientException(format("Failed to generate thymeleaf document with identifier %s. Http status: %s, Http message: %s", templateIdentifier, status.getStatusCode(), status.getReasonPhrase()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public byte[] generateWordDocument(final JsonObject jsonData, final String templateIdentifier, final UUID userId) throws IOException {
        Response response = null;
        try {
            Client client = httpClientFactory.getClient();

            response = callWordService(client, jsonData, templateIdentifier, userId);

            final Status status = fromStatusCode(response.getStatus());
            if (OK.equals(status)) {
                final InputStream is = response.readEntity(InputStream.class);
                return IOUtils.toByteArray(is);
            }
            throw new DocumentGeneratorClientException(format("Failed to generate document with identifier %s. Http status: %s, Http message: %s", templateIdentifier, status.getStatusCode(), status.getReasonPhrase()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private Response callPDFService(final Client client, final JsonObject jsonData,
                                    final String templateIdentifier, final UUID userId) {
        final JsonObject payload = getJsonBuilderFactory().createObjectBuilder()
                .add(KEY_TEMPLATE_NAME, templateIdentifier)
                .add(KEY_TEMPLATE_PAYLOAD, jsonData)
                .add(KEY_CONVERSION_FORMAT, DOCUMENT_CONVERSION_FORMAT_PDF)
                .build();

        final Invocation.Builder builder = client.target(baseUri).path(URL_ENDS_WITH)
                .request()
                .header(USER_ID, userId.toString());

        return builder.post(entity(payload, RENDER_REQUEST_TYPE));
    }

    private Response callThymeleafService(final Client client, final JsonObject jsonData,
                                          final String templateIdentifier) {


        final JsonObject payload = getJsonBuilderFactory().createObjectBuilder()
                .add(KEY_TEMPLATE_NAME, templateIdentifier)
                .add(KEY_TEMPLATE_PAYLOAD, jsonData)
                .build();

        final Invocation.Builder builder = client.target(baseUri).path(URL_ENDS_WITH)
                .request()
                .header(USER_ID, getSystemUser().toString());

        return builder.post(entity(payload, THYMELEAF_REQUEST_TYPE));
    }

    private Response callWordService(final Client client, final JsonObject jsonData,
                                     final String templateIdentifier, final UUID userId) {
        final JsonObject payload = getJsonBuilderFactory().createObjectBuilder()
                .add(KEY_TEMPLATE_NAME, templateIdentifier)
                .add(KEY_TEMPLATE_PAYLOAD, jsonData)
                .add(KEY_CONVERSION_FORMAT, DOCUMENT_CONVERSION_FORMAT_DOCX)
                .build();

        final Invocation.Builder builder = client.target(baseUri).path(URL_ENDS_WITH)
                .request()
                .header(USER_ID, userId.toString());

        return builder.post(entity(payload, RENDER_REQUEST_TYPE));
    }

    private UUID getSystemUser() {
        return systemUserProvider.getContextSystemUserId()
                .orElseThrow(() -> new RuntimeException("systemUserProvider.getContextSystemUserId() not available"));
    }

}
