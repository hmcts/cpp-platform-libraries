package uk.gov.moj.cpp.accesscontrol.refdata.providers;

import com.google.common.io.Resources;
import org.apache.commons.lang3.text.StrBuilder;
import org.glassfish.json.JsonProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;
import static uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory.createEnveloper;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithDefaults;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;
import static uk.gov.moj.cpp.accesscontrol.refdata.providers.RbacProvider.REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS;

public class RbacProviderBaseTest {

    protected static UUID documentTypeId, userId;

    @Spy
    protected final Enveloper enveloper = createEnveloper();

    @Mock
    protected Function<Object, JsonEnvelope> function;

    @Mock
    protected Function<Object, JsonEnvelope> functionForUser;

    @Captor
    protected ArgumentCaptor<Envelope<JsonObject>> envelopeCaptor;

    @Captor
    protected ArgumentCaptor<JsonEnvelope> jsonEnvelopeCaptor;

    @Mock
    protected JsonEnvelope envelope;

    @Mock
    protected Requester requester;
    @InjectMocks
    protected RbacProvider rbacProvider;

    protected static JsonObject buildCourtDocumentObject() {

        final JsonObject courtDocument =
                getJsonBuilderFactory().createObjectBuilder().add("courtDocument",
                                getJsonBuilderFactory().createObjectBuilder()
                                .add("courtDocumentId", "2279b2c3-b0d3-4889-ae8e-1ecc20c39e27")
                                .add("name", "SJP Notice")
                                .add("documentTypeId", documentTypeId.toString())
                                .add("documentTypeDescription", "SJP Notice")
                                .add("mimeType", "pdf")
                                .add("materials", getJsonBuilderFactory().createObjectBuilder().add("id", "5e1cc18c-76dc-47dd-99c1-d6f87385edf1"))
                                .add("containsFinancialMeans", false))
                        .build();

        return courtDocument;
    }

    protected static JsonObject buildEmptyCourtDocumentObject() {

        final JsonProviderImpl jsonProvider = new JsonProviderImpl();
        final JsonObject courtDocument =
                jsonProvider.createObjectBuilder().add("courtDocument",
                        jsonProvider.createObjectBuilder())
                        .build();

        return courtDocument;
    }

    protected static JsonObject buildGroups() {
        return getJsonBuilderFactory().createObjectBuilder()
                .add("groups", getJsonBuilderFactory().createArrayBuilder().add(getJsonBuilderFactory()
                        .createObjectBuilder()
                        .add("groupId", UUID.randomUUID().toString())
                        .add("groupName", "Listing Officer")
                        .build()).build()).build();
    }

    protected void setUpMockDataForReferenceAndUserContext(final JsonObject referenceDocumentTypeData) {
        final JsonEnvelope mockRefDataEnvelope = JsonEnvelope.envelopeFrom(metadataWithRandomUUID(REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS).withUserId(userId.toString()), referenceDocumentTypeData);

        when(requester.request(any())).thenReturn(mockRefDataEnvelope, JsonEnvelope.envelopeFrom(metadataWithDefaults(), buildGroups()));
    }

    protected Action buildAction() {

        final JsonEnvelope requestMessage = JsonEnvelope.envelopeFrom(
                MetadataBuilderFactory.metadataWithRandomUUID("progression.add-court-document").withUserId(userId.toString()),
                buildCourtDocumentObject());
        return new Action(requestMessage);
    }

    protected Action buildEmptyAction() {

        final JsonEnvelope requestMessage = JsonEnvelope.envelopeFrom(
                MetadataBuilderFactory.metadataWithRandomUUID("progression.add-court-document").withUserId(userId.toString()),
                buildEmptyCourtDocumentObject());
        return new Action(requestMessage);
    }

    protected Action buildNullJsonObjectAction() {

        final JsonEnvelope requestMessage = JsonEnvelope.envelopeFrom(
                MetadataBuilderFactory.metadataWithRandomUUID("progression.add-court-document").withUserId(userId.toString()),
                JsonValue.NULL);
        return new Action(requestMessage);
    }

    protected JsonObject readFile(final String ramlPath, final Map<String, String> replaceKeyValue) {
        try {
            final String jsonString = Resources.toString(Resources.getResource(ramlPath), Charset.defaultCharset());
            final StrBuilder builder = new StrBuilder(jsonString);
            replaceKeyValue.entrySet().stream().forEach(e -> {
                builder.replaceAll(e.getKey(), e.getValue());
            });
            try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(builder.build()))) {
                return jsonReader.readObject();
            }
        } catch (final Exception e) {
            return null;
        }
    }

    @BeforeEach
    public void setup() {
        documentTypeId = randomUUID();
        userId = randomUUID();
    }
}
