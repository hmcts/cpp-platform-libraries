package uk.gov.moj.cpp.accesscontrol.progression.providers;

import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonString;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.json.JsonValue.ValueType.NULL;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;
import static uk.gov.moj.cpp.accesscontrol.progression.providers.ProsecutingAuthority.isCPSProsecutedCase;

@Provider
@ApplicationScoped
public class ProgressionProvider {

    private static final String MATERIAL_ID_KEY = "materialId";

    private static final String QUERY_SEARCH_BY_MATERIAL_ID = "progression.query.cases-search-by-material-id";

    private static final String QUERY_USER_GROUPS_BY_MATERIAL_ID = "progression.query.usergroups-by-material-id";

    private static final String ALLOWED_USER_GROUPS = "allowedUserGroups";

    private static final String ATTRIBUTE_PROSECUTING_AUTHORITY = "prosecutingAuthority";

    @Inject
    Enveloper enveloper;

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    private Requester requester;

    public boolean isMaterialFromCPSProsecutedCase(final Action action) {
        return isCPSProsecutedCase.apply(getProsecutingAuthority(buildMaterialRequestEnvelope(action, QUERY_SEARCH_BY_MATERIAL_ID)));
    }

    private String getProsecutingAuthority(final JsonEnvelope requestEnvelope) {
        String prosecutingAuthority = null;
        final JsonEnvelope envelope = requester.requestAsAdmin(requestEnvelope);
        if (envelope.payload().getValueType() != NULL && envelope.payloadAsJsonObject().get(ATTRIBUTE_PROSECUTING_AUTHORITY) != null) {
            prosecutingAuthority = envelope.payloadAsJsonObject().getString(ATTRIBUTE_PROSECUTING_AUTHORITY);
        }
        return prosecutingAuthority;
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


    public List<String> getAllowedUserGroups(Action action) {
        List<String> stringList = this.getUserGroupsByMaterialId(this.buildMaterialRequestEnvelope(action, QUERY_USER_GROUPS_BY_MATERIAL_ID));
        return stringList;
    }

    private List<String> getUserGroupsByMaterialId(JsonEnvelope requestEnvelope) {
        JsonEnvelope envelope = this.requester.requestAsAdmin(requestEnvelope);
        return (List)envelope.payloadAsJsonObject().getJsonArray(ALLOWED_USER_GROUPS).getValuesAs(JsonString.class).stream().map((jsonString) -> {
            return jsonString.getString();
        }).collect(Collectors.toList());
    }

    private JsonEnvelope buildMaterialRequestEnvelope(Action action, String actionName) {
        return envelopeFrom(
                metadataBuilder().withId(UUID.randomUUID()).withName(actionName),
                jsonBuilderFactory.createObjectBuilder().add("q", this.materialIdFrom(action).toString()).build()
        );
    }







}
