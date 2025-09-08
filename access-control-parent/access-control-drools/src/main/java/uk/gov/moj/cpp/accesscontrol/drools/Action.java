package uk.gov.moj.cpp.accesscontrol.drools;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import java.util.Optional;

import javax.json.JsonValue;

/**
 * Representation of an action for convenience use with drools integration.
 */
public class Action {

    private JsonEnvelope envelope;

    public Action(final JsonEnvelope envelope) {
        this.envelope = envelope;
    }

    public String name() {
        return metadata().name();
    }

    public Optional<String> userId() {
        return metadata().userId();
    }

    public JsonValue payload() {
        return envelope.payload();
    }

    public Metadata metadata() {
        return envelope.metadata();
    }

    public JsonEnvelope envelope() {
        return envelope;
    }
}
