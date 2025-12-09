package uk.gov.justice.services.audit.client;

import org.slf4j.Logger;
import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.converter.ZonedDateTimes;
import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.audit.AuditClient;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjects;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.function.Function;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

/**
 * Sends audit events to the Audit topic, to be latest consumed by the Audit Context.
 */
@SuppressWarnings("java:S1123")
@ApplicationScoped
@Alternative
@Priority(5)
@Deprecated(forRemoval = true)
public class RemoteAuditClient implements AuditClient {

    private static final String AUDIT_EVENT_NAME = "audit.events.audit-recorded";
    private static final String TIMESTAMP = "timestamp";
    private static final String CONTENT = "content";
    private static final String ORIGIN = "origin";
    private static final String COMPONENT = "component";
    private static final String PAYLOAD = "_payload";
    private static final String METADATA = "_metadata";

    @Inject
    Logger logger;

    @Inject
    @FrameworkComponent("AUDIT_CLIENT")
    Sender sender;

    @Inject
    Enveloper enveloper;

    @Inject
    ServiceContextNameProvider serviceContextNameProvider;

    @Inject
    Clock clock;

    @Override
    public void auditEntry(final JsonEnvelope envelope, final String component) {
        sender.send(createOutgoingEnvelopeFrom(envelope, component));
    }

    private JsonEnvelope createOutgoingEnvelopeFrom(final JsonEnvelope envelope, final String component) {
        final JsonObjectBuilder objectBuilder = getJsonBuilderFactory().createObjectBuilder()
                .add(CONTENT, createContentFrom(envelope))
                .add(ORIGIN, serviceContextNameProvider.getServiceContextName())
                .add(COMPONENT, component)
                .add(TIMESTAMP, ZonedDateTimes.toString(clock.now()));

        final Function<Object, JsonEnvelope> function = enveloper.withMetadataFrom(envelope, AUDIT_EVENT_NAME);

        return function.apply(objectBuilder.build());
    }

    private JsonObjectBuilder createContentFrom(final JsonEnvelope envelope) {
        JsonObjectBuilder contentBuilder = getJsonBuilderFactory().createObjectBuilder();
        final JsonValue payload = envelope.payload();

        if (payload != null) {
            switch (payload.getValueType()) {
                case ARRAY:
                    final JsonArrayBuilder arrayBuilder = getJsonBuilderFactory().createArrayBuilder();
                    ((JsonArray) payload).forEach(arrayBuilder::add);
                    contentBuilder.add(PAYLOAD, arrayBuilder);
                    break;

                case OBJECT:
                    contentBuilder = JsonObjects.createObjectBuilder(envelope.payloadAsJsonObject());
                    break;

                default:
                    contentBuilder.add(PAYLOAD, payload);
            }
        }

        return contentBuilder.add(METADATA, envelope.metadata().asJsonObject());
    }
}
