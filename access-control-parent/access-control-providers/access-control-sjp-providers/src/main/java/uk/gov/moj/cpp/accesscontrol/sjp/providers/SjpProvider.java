package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonValue;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;

@Provider
@ApplicationScoped
public class SjpProvider {

    public static final JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);

    private static final String QUERY_CASE_PROSECUTING_AUTHORITY_ID = "sjp.query.case-prosecuting-authority";
    private static final String CASE_ID_KEY = "caseId";

    @Inject
    Enveloper enveloper;

    @Inject
    private ProsecutingAuthorityProvider prosecutingAuthorityProvider;

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    private Requester requester;

    public boolean hasProsecutingAuthorityToCase(final Action action) {
        final JsonEnvelope envelope = requester.requestAsAdmin(buildGetCaseProsecutingAuthorityQuery(action));

        final JsonValue prosecutingAuthorityResponse = envelope.payload();

        if (JsonValue.NULL.equals(prosecutingAuthorityResponse)) {
            return true;
        } else {
            final String prosecutingAuthority = envelope.payloadAsJsonObject().getString("prosecutingAuthority");
            return prosecutingAuthorityProvider
                    .userHasProsecutingAuthorityAccess(
                            action.envelope(),
                            prosecutingAuthority);
        }
    }

    private JsonEnvelope buildGetCaseProsecutingAuthorityQuery(final Action action) {
        return enveloper.withMetadataFrom(action.envelope(), QUERY_CASE_PROSECUTING_AUTHORITY_ID)
                .apply(jsonBuilderFactory.createObjectBuilder()
                        .add(CASE_ID_KEY, caseIdFrom(action).toString())
                        .build());
    }

    private UUID caseIdFrom(final Action action) {
        return ofNullable(
                action.envelope().payloadAsJsonObject().getString(CASE_ID_KEY))
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException(format("Action should contain %s", CASE_ID_KEY)));
    }

}
