package uk.gov.moj.cpp.system.documentgenerator.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.common.http.HeaderConstants.USER_ID;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.test.utils.core.random.RandomGenerator.STRING;

@ExtendWith(MockitoExtension.class)
public class DefaultDocumentGeneratorClientTest {

    private static final String BASE_URI = STRING.next();
    private static final String URL_ENDS_WITH = "/render";

    private static final String DOCUMENT_CONVERSION_FORMAT = "pdf";
    private static final String DOCUMENT_CONVERSION_FORMAT_WORD = "docx";
    private static final String KEY_TEMPLATE_NAME = "templateName";
    private static final String KEY_CONVERSION_FORMAT = "conversionFormat";
    private static final String KEY_TEMPLATE_PAYLOAD = "templatePayload";

    private static final String TEMPLATE_IDENTIFIER = STRING.next();
    private static final String PDF_DOCUMENT_AS_STRING = STRING.next();
    private static final String WORD_DOCUMENT_AS_STRING = STRING.next();
    private static final UUID SYSTEM_USER_ID = randomUUID();

    private DefaultDocumentGeneratorClient target;

    @Mock
    private HttpClientFactory httpClientFactory;

    @Mock
    private SystemUserProvider systemUserProvider;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private Client client;

    @SuppressWarnings("ConstantConditions")
    @AfterEach
    public void closeClientToStopAnnoyingWarningMessageAboutUnclosedClients() throws Exception {
        if(client != null)  {
            client.close();
        }
    }

    @Mock
    private Response response;

    @Mock
    private Invocation.Builder builder;

    @Captor
    private ArgumentCaptor<Entity> requestEntityCaptor;

    @BeforeEach
    public void setup() {
        target = new DefaultDocumentGeneratorClient(BASE_URI, httpClientFactory, systemUserProvider);

        given(httpClientFactory.getClient()).willReturn(client);
        given(client.target(BASE_URI).path(URL_ENDS_WITH).request()).willReturn(builder);
        given(builder.header(USER_ID, SYSTEM_USER_ID.toString())).willReturn(builder);
        given(builder.post(any(Entity.class))).willReturn(response);
    }

    @Test
    public void shouldGenerateDocument() throws IOException {
        given(response.getStatus()).willReturn(200);
        given(response.readEntity(InputStream.class)).willReturn(new ByteArrayInputStream(PDF_DOCUMENT_AS_STRING.getBytes()));

        final JsonObject jsonTemplateData = prepareJsonDataForTemplate();
        final byte[] result = target.generatePdfDocument(jsonTemplateData, TEMPLATE_IDENTIFIER, SYSTEM_USER_ID);

        assertThat(PDF_DOCUMENT_AS_STRING, is(new String(result)));

        verify(builder).post(requestEntityCaptor.capture());

        final Entity actualEntity = requestEntityCaptor.getValue();
        final JsonObject jsonObject = (JsonObject) actualEntity.getEntity();
        assertThat(jsonObject.getString(KEY_TEMPLATE_NAME), is(TEMPLATE_IDENTIFIER));
        assertThat(jsonObject.getString(KEY_CONVERSION_FORMAT), is(DOCUMENT_CONVERSION_FORMAT));
        assertThat(jsonObject.getJsonObject(KEY_TEMPLATE_PAYLOAD), is(jsonTemplateData));
    }

    @Test
    public void shouldGenerateWordDocument() throws IOException {
        given(response.getStatus()).willReturn(200);
        given(response.readEntity(InputStream.class)).willReturn(new ByteArrayInputStream(WORD_DOCUMENT_AS_STRING.getBytes()));

        final JsonObject jsonTemplateData = prepareJsonDataForTemplate();
        final byte[] result = target.generateWordDocument(jsonTemplateData, TEMPLATE_IDENTIFIER, SYSTEM_USER_ID);

        assertThat(WORD_DOCUMENT_AS_STRING, is(new String(result)));

        verify(builder).post(requestEntityCaptor.capture());

        final Entity actualEntity = requestEntityCaptor.getValue();
        final JsonObject jsonObject = (JsonObject) actualEntity.getEntity();
        assertThat(jsonObject.getString(KEY_TEMPLATE_NAME), is(TEMPLATE_IDENTIFIER));
        assertThat(jsonObject.getString(KEY_CONVERSION_FORMAT), is(DOCUMENT_CONVERSION_FORMAT_WORD));
        assertThat(jsonObject.getJsonObject(KEY_TEMPLATE_PAYLOAD), is(jsonTemplateData));
    }

    @Test
    public void shouldThrowDocumentGeneratorClientExceptionWhenResponseStatusCodeIsNot200() throws IOException {
        given(response.getStatus()).willReturn(500);

        final JsonObject jsonTemplateData = prepareJsonDataForTemplate();

        final DocumentGeneratorClientException documentGeneratorClientException = assertThrows(
                DocumentGeneratorClientException.class,
                () -> target.generatePdfDocument(jsonTemplateData, TEMPLATE_IDENTIFIER, SYSTEM_USER_ID));

        assertThat(documentGeneratorClientException.getMessage(), is(format("Failed to generate document with identifier %s. Http status: %s, Http message: %s",
                TEMPLATE_IDENTIFIER, 500, "Internal Server Error")));
    }

    private JsonObject prepareJsonDataForTemplate() {
        return getJsonBuilderFactory().createObjectBuilder()
                .add("nowText", STRING.next())
                .build();
    }
}
