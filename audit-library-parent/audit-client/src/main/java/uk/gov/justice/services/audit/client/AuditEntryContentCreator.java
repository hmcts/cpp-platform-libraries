package uk.gov.justice.services.audit.client;

import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.common.converter.ZonedDateTimes;
import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

@ApplicationScoped
public class AuditEntryContentCreator {
    private static final String TIMESTAMP = "timestamp";
    private static final String CONTENT = "content";
    private static final String ORIGIN = "origin";
    private static final String COMPONENT = "component";
    private static final String PAYLOAD = "_payload";
    private static final String METADATA = "_metadata";

    @Inject
    private ServiceContextNameProvider serviceContextNameProvider;

    @Inject
    private Clock clock;


    //To minimize the changes on RemoteAuditClient existing functionality, this code is duplicated. RemoveAuditClient is marked as deprecated and will be removed in future.
    public JsonObject create(final JsonEnvelope envelope, final String component) {
        return getJsonBuilderFactory().createObjectBuilder()
                .add(CONTENT, createContentFrom(envelope))
                .add(ORIGIN, serviceContextNameProvider.getServiceContextName())
                .add(COMPONENT, component)
                .add(TIMESTAMP, ZonedDateTimes.toString(clock.now()))
                .build();

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
