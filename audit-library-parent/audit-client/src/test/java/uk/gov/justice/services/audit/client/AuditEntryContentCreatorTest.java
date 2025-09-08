package uk.gov.justice.services.audit.client;

import static java.util.UUID.randomUUID;
import static javax.json.Json.createArrayBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.MetadataBuilder;
import uk.gov.justice.services.messaging.spi.DefaultJsonEnvelopeProvider;
import uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder;

import java.time.ZonedDateTime;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuditEntryContentCreatorTest {

    @Mock
    private ServiceContextNameProvider serviceContextNameProvider;

    @Mock
    private Clock clock;

    @InjectMocks
    private AuditEntryContentCreator auditEntryContentCreator;

    @Test
    void shouldHandleJsonObjectPayload() {
        final MetadataBuilder metadataBuilder = new DefaultJsonEnvelopeProvider().metadataBuilder().withName("testEvent").withId(randomUUID());
        final JsonEnvelope envelope = JsonEnvelopeBuilder.envelope().with(metadataBuilder).withPayloadOf("value", "key").build();
        when(serviceContextNameProvider.getServiceContextName()).thenReturn("testContext");
        when(clock.now()).thenReturn(ZonedDateTime.parse("2023-01-01T01:02:03.123Z"));

        JsonObject result = auditEntryContentCreator.create(envelope, "API");

        assertThat(result.getString("origin"), is("testContext"));
        assertThat(result.getString("component"), is("API"));
        assertThat(result.getString("timestamp"), is("2023-01-01T01:02:03.123Z"));
        assertThat(result.getJsonObject("content").getString("key"), is("value"));
        assertThat(result.getJsonObject("content").getJsonObject("_metadata").getString("name"), is("testEvent"));
    }

    @Test
    void shouldHandleNullPayload() {
        final MetadataBuilder metadataBuilder = new DefaultJsonEnvelopeProvider().metadataBuilder().withName("testEvent").withId(randomUUID());
        final JsonEnvelope envelope = JsonEnvelopeBuilder.envelope().withNullPayload().with(metadataBuilder).build();
        when(serviceContextNameProvider.getServiceContextName()).thenReturn("testContext");
        when(clock.now()).thenReturn(ZonedDateTime.parse("2023-01-01T00:00:00Z"));

        JsonObject result = auditEntryContentCreator.create(envelope, "API");

        assertThat(result.getJsonObject("content").getJsonObject("_metadata").getString("name"), is("testEvent"));
    }

    @Test
    void shouldHandleArrayPayload() {
        JsonArray arrayPayload = createArrayBuilder().add("value1").add("value2").build();
        final MetadataBuilder metadataBuilder = new DefaultJsonEnvelopeProvider().metadataBuilder().withName("testEvent").withId(randomUUID());
        final JsonEnvelope envelope = new DefaultJsonEnvelopeProvider().envelopeFrom(metadataBuilder.build(), arrayPayload);
        when(serviceContextNameProvider.getServiceContextName()).thenReturn("testContext");
        when(clock.now()).thenReturn(ZonedDateTime.parse("2023-01-01T00:00:00Z"));

        JsonObject result = auditEntryContentCreator.create(envelope, "API");

        assertThat(result.getJsonObject("content").getJsonArray("_payload").getString(0), is("value1"));
        assertThat(result.getJsonObject("content").getJsonArray("_payload").getString(1), is("value2"));
        assertThat(result.getJsonObject("content").getJsonObject("_metadata").getString("name"), is("testEvent"));
    }
}