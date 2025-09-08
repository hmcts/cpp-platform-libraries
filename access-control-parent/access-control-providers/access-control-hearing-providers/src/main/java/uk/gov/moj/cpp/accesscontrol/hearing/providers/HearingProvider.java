package uk.gov.moj.cpp.accesscontrol.hearing.providers;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.json.Json.createObjectBuilder;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;

import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonString;

@Provider
@ApplicationScoped
public class HearingProvider {

    private static final String MATERIAL_ID_KEY = "materialId";

    private static final String QUERY_SEARCH_BY_MATERIAL_ID = "hearing.query.search-by-material-id";

    private static final String QUERY_SEARCH_BY_HEARING_ID = "hearing.get.hearing";

    private static final String ALLOWED_USER_GROUPS = "allowedUserGroups";

    private static final String ID_KEY = "id";

    private static final String HEARING_ID_KEY = "hearingId";

    private static final String USER_ID_KEY = "userId";

    private static final String APPROVALS_REQUESTED = "approvalsRequested";
    private static final String HEARING = "hearing";

    @Inject
    Enveloper enveloper;

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    private Requester requester;


    public boolean isUserAllowedToApproveResultAmendment(final Action action) {
        return isUserAllowedToApproveResultAmendment(buildHearingRequestEnvelope(action), userIdFrom(action));
    }

    public List<String> getAllowedUserGroups(final Action action) {
        return getUserGroupsByMaterialIdFromHearing(buildMaterialRequestEnvelope(action));
    }

    private List<String> getUserGroupsByMaterialIdFromHearing(final JsonEnvelope requestEnvelope) {
        JsonEnvelope envelope = requester.requestAsAdmin(requestEnvelope);
        return envelope.payloadAsJsonObject().getJsonArray(ALLOWED_USER_GROUPS).getValuesAs(JsonString.class).stream().map(jsonString -> jsonString.getString()).collect(Collectors.toList());
    }

    private JsonEnvelope buildMaterialRequestEnvelope(final Action action) {
        return enveloper.withMetadataFrom(action.envelope(), QUERY_SEARCH_BY_MATERIAL_ID)
                .apply(createObjectBuilder()
                        .add("q", materialIdFrom(action).toString())
                        .build());
    }

    private UUID materialIdFrom(final Action action) {
        return ofNullable(
                action.envelope()
                        .payloadAsJsonObject()
                        .getString(MATERIAL_ID_KEY))
                .map(UUID::fromString)
                .orElseThrow(() ->
                        new IllegalArgumentException(format("Action should contain %s", MATERIAL_ID_KEY)));
    }

    private boolean isUserAllowedToApproveResultAmendment(final JsonEnvelope requestEnvelope, final UUID requestUserId) {
        final JsonEnvelope envelope = requester.requestAsAdmin(requestEnvelope);
        final JsonArray jsonArray = envelope.payloadAsJsonObject().getJsonObject(HEARING).getJsonArray(APPROVALS_REQUESTED);
        for (int i = 0; i < jsonArray.size(); i++) {
            final JsonString userId = jsonArray.getJsonObject(i).getJsonString(USER_ID_KEY);
            if (userId.getString().equalsIgnoreCase(requestUserId.toString())) {
                return false;
            }
        }
        return true;
    }

    private JsonEnvelope buildHearingRequestEnvelope(final Action action) {
        return enveloper.withMetadataFrom(action.envelope(), QUERY_SEARCH_BY_HEARING_ID)
                .apply(createObjectBuilder()
                        .add(HEARING_ID_KEY, hearingIdFrom(action).toString())
                        .build());
    }

    private UUID hearingIdFrom(final Action action) {
        return ofNullable(
                action.envelope()
                        .payloadAsJsonObject()
                        .getString(ID_KEY))
                .map(UUID::fromString)
                .orElseThrow(() ->
                        new IllegalArgumentException(format("Action should contain %s", HEARING_ID_KEY)));
    }

    private UUID userIdFrom(final Action action) {
        return ofNullable(
                action.envelope()
                        .payloadAsJsonObject()
                        .getString(USER_ID_KEY))
                .map(UUID::fromString)
                .orElseThrow(() ->
                        new IllegalArgumentException(format("Action should contain %s", USER_ID_KEY)));
    }
}
