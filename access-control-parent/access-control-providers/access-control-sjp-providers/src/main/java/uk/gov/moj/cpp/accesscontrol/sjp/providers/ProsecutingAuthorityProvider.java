package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import org.slf4j.Logger;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import static java.lang.Boolean.valueOf;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;
import static uk.gov.moj.cpp.accesscontrol.sjp.providers.SjpProvider.jsonBuilderFactory;

@Provider
@ApplicationScoped
public class ProsecutingAuthorityProvider {

    static final String ACCESS_CONTROL_DISABLED_PROPERTY = "uk.gov.justice.services.core.accesscontrol.disabled";

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    private Requester requester;

    @Inject
    private Enveloper enveloper;

    @Inject
    private Logger logger;

    /**
     * Checks if the current user has access to a Prosecuting Authority
     *
     * @param envelope             envelope of request
     * @param prosecutingAuthority name of the Prosecuting Authority
     * @return true if has access, false otherwise
     */
    public boolean userHasProsecutingAuthorityAccess(final JsonEnvelope envelope, final String prosecutingAuthority) {

        final ProsecutingAuthorityAccess usersProsecutingAuthorityAccess = getCurrentUsersProsecutingAuthorityAccess(envelope);

        return usersProsecutingAuthorityAccess.hasAccess(prosecutingAuthority);
    }

    public ProsecutingAuthorityAccess getCurrentUsersProsecutingAuthorityAccess(final JsonEnvelope envelope) {

        if (isAccessControlDisabled()) {
            this.logger.trace("Skipping prosecuting authority access control due to configuration");
            return ProsecutingAuthorityAccess.ALL;
        } else {
            this.logger.trace("Performing prosecuting authority access control for action: {}", envelope.metadata().name());
            final JsonEnvelope requestEnvelope = enveloper.withMetadataFrom(envelope, "usersgroups.get-user-details")
                    .apply(buildRequestPayload(envelope));
            return buildFromResponseJson(requester.requestAsAdmin(requestEnvelope).payloadAsJsonObject());
        }
    }

    private ProsecutingAuthorityAccess buildFromResponseJson(final JsonObject responsePayload) {

        return ProsecutingAuthorityAccess.of(responsePayload.getString("prosecutingAuthorityAccess", null));
    }

    private JsonValue buildRequestPayload(final JsonEnvelope envelope) {
        final JsonObjectBuilder objectBuilder = jsonBuilderFactory.createObjectBuilder();

        if (envelope.metadata().userId().isPresent()) {
            objectBuilder.add("userId", envelope.metadata().userId().get());
        } else {
            objectBuilder.addNull("userId");
        }

        return objectBuilder.build();
    }

    private boolean isAccessControlDisabled() {
        return valueOf(System.getProperty(ACCESS_CONTROL_DISABLED_PROPERTY));
    }
}
