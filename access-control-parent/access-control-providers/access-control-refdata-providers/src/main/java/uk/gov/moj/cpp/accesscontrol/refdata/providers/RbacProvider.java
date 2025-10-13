package uk.gov.moj.cpp.accesscontrol.refdata.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.Envelope;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;

@Provider
@ApplicationScoped
public class RbacProvider {

    public static final String REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS = "referencedata.query.document-type-access";
    public static final String ID = "id";
    public static final String GROUPS = "groups";
    public static final String GROUP_NAME = "groupName";
    public static final String USERGROUPS_QUERY = "usersgroups.get-groups-by-user";
    private static final Logger LOGGER = LoggerFactory.getLogger(RbacProvider.class);
    private static final String USER_ID = "userId";
    private static final String UPLOAD_ACTION = "uploadUserGroups";
    private static final String READ_ACTION = "readUserGroups";
    private static final String DOWNLOAD_ACTION = "downloadUserGroups";
    private static final String DELETE_ACTION = "deleteUserGroups";
    private static final String COURT_DOCUMENT_TYPE_RBAC = "courtDocumentTypeRBAC";
    private static final String VALID_FROM = "validFrom";
    private static final String VALID_TO = "validTo";


    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    private Requester requester;


    public boolean isLoggedInUserAllowedToUploadDocument(final Action action) {
        return isAllowedToAccessDocumentForGivenAction(action, UPLOAD_ACTION);
    }

    public boolean isLoggedInUserAllowedToReadDocument(final Action action) {
        return isAllowedToAccessDocumentForGivenAction(action, READ_ACTION);
    }

    public boolean isLoggedInUserAllowedToDownloadDocument(final Action action) {
        return isAllowedToAccessDocumentForGivenAction(action, DOWNLOAD_ACTION);
    }


    public boolean isLoggedInUserAllowedToDeleteDocument(final Action action) {
        return isAllowedToAccessDocumentForGivenAction(action, DELETE_ACTION);
    }

    private boolean isAllowedToAccessDocumentForGivenAction(final Action action, final String userAction) {

        LOGGER.info("envelope for courtDocument {}", action.envelope().payload());

        if (action.envelope().payload() == JsonValue.NULL) {
            return true;
        }

        if ((containsKey(action.envelope().payloadAsJsonObject(), "courtDocument") &&
                !containsKey(action.envelope().payloadAsJsonObject().getJsonObject("courtDocument"), "documentTypeId"))) {
            return false;
        }

        final Optional<List<String>> listOfVaildUserGroup = getListOfValidUserGroup(getDocumentTypeAccessData(action.envelope()), userAction);

        return listOfVaildUserGroup.filter(documentTypeAccessGroup -> isUserGroupsMatchesWithRBAC(documentTypeAccessGroup, getUserGroups(action))).isPresent();

    }


    private JsonObject getDocumentTypeAccessData(final JsonEnvelope envelope) {


        final String documentTypeId = envelope.payloadAsJsonObject().getJsonObject("courtDocument").getString("documentTypeId");

        final Envelope<JsonObject> jsonObjectEnvelope = Enveloper.envelop(jsonBuilderFactory.createObjectBuilder().add(ID, documentTypeId).build())
                .withName(REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS)
                .withMetadataFrom(envelope);

        final JsonEnvelope response = requester.request(jsonObjectEnvelope);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(" '{}' by id {} received with payload {} ", REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS, documentTypeId, response != null ? response.toObfuscatedDebugString() : "");
        }
        return response != null ? response.payloadAsJsonObject() : null;
    }


    private boolean containsKey(final JsonObject jsonObject, final String key) {
        if (jsonObject != null && jsonObject.containsKey(key) &&
                jsonObject.get(key) != JsonValue.NULL &&
                jsonObject.get(key) != null) {
            return true;
        }
        return false;
    }


    private Optional<List<String>> getListOfValidUserGroup(final JsonObject documentTypeData, final String userAction) {
        if (documentTypeData == null
                || !documentTypeData.containsKey(COURT_DOCUMENT_TYPE_RBAC)
                || !documentTypeData.getJsonObject(COURT_DOCUMENT_TYPE_RBAC).containsKey(userAction)
                || documentTypeData.getJsonObject(COURT_DOCUMENT_TYPE_RBAC).getJsonArray(userAction).isEmpty()) {
            return Optional.empty();
        }
        final JsonArray courtDocumentTypeRBAC = documentTypeData.getJsonObject(COURT_DOCUMENT_TYPE_RBAC).getJsonArray(userAction);


        final List<String> result = IntStream.range(0, courtDocumentTypeRBAC.size())
                .mapToObj(courtDocumentTypeRBAC::getJsonObject)
                .filter(group -> group.getJsonObject("cppGroup") != null)
                .filter(this::isValidByDate).map(group -> group.getJsonObject("cppGroup"))
                .filter(group -> isNoneBlank(group.getString(GROUP_NAME)))
                .map(group -> group.getString(GROUP_NAME)).collect(toList());

        return Optional.of(result);

    }

    private boolean isValidByDate(final JsonObject group) {
        final LocalDate validFrom = group.containsKey(VALID_FROM) && isNoneBlank(group.getString(VALID_FROM)) ? LocalDate.parse(group.getString(VALID_FROM)) : null;
        final LocalDate validTo = group.containsKey(VALID_TO) && isNoneBlank(group.getString(VALID_TO)) ? LocalDate.parse(group.getString(VALID_TO)) : null;
        if (validFrom == null && validTo == null) {
            return true;
        }

        final LocalDate today = LocalDate.now();

        if (validTo == null) {
            return today.isAfter(validFrom) || today.isEqual(validFrom);
        }

        if (validFrom == null) {
            return today.isBefore(validTo) || today.isEqual(validTo);
        }


        return (today.isAfter(validFrom) || today.isEqual(validFrom)) && (today.isBefore(validTo) || today.isEqual(validTo));
    }


    private boolean isUserGroupsMatchesWithRBAC(final List<String> groupsWithCreateAccess, final List<String> userGroups) {
        return groupsWithCreateAccess.stream().anyMatch(userGroups::contains);
    }

    private List<String> getUserGroups(final Action action) {
        final JsonObject userGroupsByUserId = getUserGroupsByUserId(action);

        return userGroupsByUserId != null ? userGroupsByUserId.getJsonArray(GROUPS)
                .getValuesAs(JsonObject.class)
                .stream()
                .map(o -> o.getString(GROUP_NAME))
                .collect(toList()) : emptyList();
    }

    private JsonObject getUserGroupsByUserId(final Action action) {
        JsonObject userGroups = null;
        final String userId = action.metadata().userId().orElseThrow(() -> new RuntimeException("UserId missing from the command."));


        final JsonObject payload = jsonBuilderFactory.createObjectBuilder()
                .add(USER_ID, userId)
                .build();

        final Envelope<JsonObject> jsonObjectEnvelope = Enveloper.envelop(payload)
                .withName(USERGROUPS_QUERY)
                .withMetadataFrom(action.envelope());

        final JsonEnvelope response = requester.request(jsonObjectEnvelope);
        if (response.payload().getValueType() != JsonValue.ValueType.NULL) {
            userGroups = response.payloadAsJsonObject();
        }
        return userGroups;
    }


}
