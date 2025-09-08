package uk.gov.justice.services.audit.client;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.util.UUID.randomUUID;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.json.JsonValue.NULL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory.createEnveloper;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.metadata;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payloadIsJson;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;

import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.common.helper.StoppedClock;
import uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder;

import java.util.UUID;

import javax.json.JsonValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class RemoteAuditClientTest {

    private static final String CONTEXT_NAME = "context-command-api";
    private static final String COMPONENT = "test-component";
    private static final UUID UUID = randomUUID();
    private static final String ACTION_NAME = "some.action";

    @Mock
    private Logger logger;

    @Mock
    private Sender sender;

    @Spy
    private Enveloper enveloper = createEnveloper();

    @Mock
    private ServiceContextNameProvider serviceContextNameProvider;

    @Spy
    StoppedClock clock = new StoppedClock(now(UTC));

    @InjectMocks
    private RemoteAuditClient remoteAuditClient;

    private JsonEnvelope envelope;

    @BeforeEach
    public void setup() {
        when(serviceContextNameProvider.getServiceContextName()).thenReturn(CONTEXT_NAME);
        envelope = createEnvelope();
    }

    @Test
    public void shouldVerifyAuditMessageSent() throws Exception {
        remoteAuditClient.auditEntry(envelope, COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content.field", equalTo("value")),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    @Test
    public void shouldVerifyJsonNullPayloadAuditMessageSent() throws Exception {
        remoteAuditClient.auditEntry(envelopeFrom(metadataOf(UUID, ACTION_NAME), NULL), COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    @Test
    public void shouldVerifyJsonArrayPayloadAuditMessageSent() throws Exception {
        remoteAuditClient.auditEntry(envelopeFrom(metadataOf(UUID, ACTION_NAME),
                createArrayBuilder()
                        .add("Id")
                        .add("Name")
                        .build()),
                COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content._payload[0]", equalTo("Id")),
                        withJsonPath("$.content._payload[1]", equalTo("Name")),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    @Test
    public void shouldVerifyJsonStringPayloadAuditMessageSent() throws Exception {
        remoteAuditClient.auditEntry(envelopeFrom(metadataOf(UUID, ACTION_NAME),
                createObjectBuilder()
                        .add("name", "value")
                        .build()
                        .getJsonString("name")),
                COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content._payload", equalTo("value")),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    @Test
    public void shouldVerifyJsonNumberPayloadAuditMessageSent() throws Exception {
        remoteAuditClient.auditEntry(envelopeFrom(metadataOf(UUID, ACTION_NAME),
                createObjectBuilder()
                        .add("count", 15)
                        .build()
                        .getJsonNumber("count")),
                COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content._payload", equalTo(15)),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    @Test
    public void shouldVerifyNullPayloadAuditMessageSent() throws Exception {
        final JsonValue jsonValue = null;
        remoteAuditClient.auditEntry(envelopeFrom(metadataOf(UUID, ACTION_NAME), jsonValue), COMPONENT);

        final ArgumentCaptor<JsonEnvelope> argumentCaptor = forClass(JsonEnvelope.class);
        verify(sender).send(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), jsonEnvelope(
                metadata()
                        .withName("audit.events.audit-recorded")
                        .withCausationIds(UUID),
                payloadIsJson(allOf(
                        withJsonPath("$.origin", equalTo(CONTEXT_NAME)),
                        withJsonPath("$.component", equalTo(COMPONENT)),
                        withJsonPath("$.timestamp", notNullValue()),
                        withJsonPath("$.content", notNullValue()),
                        withJsonPath("$.content._metadata.id", equalTo(UUID.toString())),
                        withJsonPath("$.content._metadata.name", equalTo(ACTION_NAME))
                ))));
    }

    private JsonEnvelope createEnvelope() {
        return JsonEnvelopeBuilder.envelope()
                .with(metadataOf(UUID, ACTION_NAME))
                .withPayloadOf("value", "field")
                .build();
    }
}
