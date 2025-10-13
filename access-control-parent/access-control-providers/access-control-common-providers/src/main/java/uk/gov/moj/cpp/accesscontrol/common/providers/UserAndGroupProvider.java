package uk.gov.moj.cpp.accesscontrol.common.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.drools.ExpectedPermission;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.gov.justice.services.messaging.Envelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;

@Provider
@ApplicationScoped
public class UserAndGroupProvider {

    private static final String GROUPS = "groups";
    private static final String GROUP_NAME = "groupName";
    private static final String USERGROUPS_QUERY = "usersgroups.get-groups-by-user";
    private static final String USERPERMISSIONS_QUERY = "usersgroups.get-logged-in-user-permissions";
    private static final String PERMISSIONS = "permissions";

    private static final String USER_ID = "userId";

    private final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();

    @Inject
    Logger logger;

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    Requester requester;

    @Inject
    Enveloper enveloper;



    static final String GROUP_NAME_SYSTEM_USERS = "System Users";

    /***
     * Checks if user is in the "System Users" group.
     *
     * @param action action containing userId
     * @return true if user is in the "System Users" group
     */
    public boolean isSystemUser(final Action action) {
        return isMemberOfAnyOfTheSuppliedGroups(action, GROUP_NAME_SYSTEM_USERS);
    }

    /***
     * Checks if user is a member of any of the supplied groups.
     *
     * @param action     action containing userId
     * @param groupNames array of group names to check
     * @return true if user is in any of groups outlined in groupNames.
     */
    public boolean isMemberOfAnyOfTheSuppliedGroups(final Action action, final String... groupNames) {
        JsonObject userGroupsJson = getUserGroupsByUserId(action);
        return isMemberInGroups(userGroupsJson, Arrays.asList(groupNames));
    }

    /***
     * Checks if user is a member of any of the supplied groups.
     *
     * @param action     action containing userId
     * @param groupNames list of group names to check
     * @return true if user is in any of groups outlined in groupNames.
     */
    public boolean isMemberOfAnyOfTheSuppliedGroups(final Action action, final List<String> groupNames) {
        JsonObject userGroupsJson = getUserGroupsByUserId(action);
        return isMemberInGroups(userGroupsJson, groupNames);
    }

    /**
     * Checks if user belongs to a group
     * <p>
     * Deprecated - the access modifier on this method needs to be changed to private.
     *
     * @param action    action containing userId
     * @param groupName name of the group
     * @return true if user is part of the group, false otherwise
     */
    @Deprecated
    public boolean isInGroup(final Action action, final String groupName) {
        return isUserInGroup(action, groupName);
    }

    public boolean hasPermission(Action action, final String... expectedPermissions) {

        logger.info("Method hasPermission called with expectedPermission {} ", expectedPermissions);

        final List<ExpectedPermission> expectedPermissionList = Arrays.asList(expectedPermissions).stream()
                .map(expectedPermissionJson -> getExpectedPermissionFromPermissionString(expectedPermissionJson))
                .collect(toList());
        final JsonArray permissions = getUserPermissionsByUserId(action);
        return isMemberInPermissionList(permissions, expectedPermissionList);
    }

    private ExpectedPermission getExpectedPermissionFromPermissionString(final String expectedPermissionJson) {
        try {
            return objectMapper.readValue(expectedPermissionJson, ExpectedPermission.class);
        } catch (IOException ioe) {
            throw new RuntimeException(format("Unable to convert Json String %s", expectedPermissionJson), ioe);
        }
    }

    private boolean isUserInGroup(final Action action, final String groupName) {
        boolean isInGrp = false;
        final Optional<String> userId = action.userId();

        if (userId.isPresent()) {
            final JsonObject payload = jsonBuilderFactory.createObjectBuilder()
                    .add(USER_ID, userId.get())
                    .add(GROUP_NAME, groupName)
                    .build();

            final JsonEnvelope envelope = envelopeFrom(
                    JsonEnvelope.metadataBuilder().withId(UUID.randomUUID()).withName(USERGROUPS_QUERY),
                    payload
            );

            final JsonEnvelope response = requester.requestAsAdmin(envelope);

            isInGrp = responseContainsGroupId(response);
        } else {
            logger.error("Action should contain userId");
        }
        return isInGrp;
    }

    private boolean responseContainsGroupId(final JsonEnvelope response) {
        boolean userPresentInGroup = false;
        if (response.payload().getValueType() != JsonValue.ValueType.NULL) {
            final JsonObject payload = response.payloadAsJsonObject();
            userPresentInGroup = payload.containsKey(GROUPS) && !payload.getJsonArray(GROUPS).isEmpty();
        }
        return userPresentInGroup;
    }


    private JsonObject getUserGroupsByUserId(final Action action) {
        JsonObject userGroups = null;
        final Optional<String> userId = action.userId();

        if (userId.isPresent()) {
            final JsonObject payload = jsonBuilderFactory.createObjectBuilder()
                    .add(USER_ID, userId.get())
                    .build();

            final JsonEnvelope envelope = enveloper.withMetadataFrom(action.envelope(), USERGROUPS_QUERY).apply(payload);

            JsonEnvelope response = requester.requestAsAdmin(envelope);
            if (response.payload().getValueType() != JsonValue.ValueType.NULL) {
                userGroups = response.payloadAsJsonObject();
            }
        }
        return userGroups;
    }

    private JsonArray getUserPermissionsByUserId(final Action action) {

        logger.info("Method getUserPermissionsByUserId called with Action {} ", action.name());
        JsonArray userPermissions = null;
        final Optional<String> userId = action.userId();

        if (userId.isPresent()) {
            final Metadata existingMetadata = action.metadata();
            final Metadata metadataWithActionUserId = metadataBuilder()
                    .withUserId(userId.get())
                    .withId(UUID.randomUUID())
                    .withName(existingMetadata.name())
                    .build();
            final JsonEnvelope jsonEnvelope = JsonEnvelope.envelopeFrom(metadataWithActionUserId, JsonValue.NULL);
            final JsonEnvelope envelope = enveloper.withMetadataFrom(jsonEnvelope, USERPERMISSIONS_QUERY).apply(action.payload());

            JsonEnvelope response = requester.request(envelope);
            logger.info("Response  from API usersgroups.get-logged-in-user-permissions {} ", response.payloadAsJsonObject());
            if (response.payload().getValueType() != JsonValue.ValueType.NULL) {
                userPermissions = response.payloadAsJsonObject().getJsonArray(PERMISSIONS);
                logger.info("got User Permissions {} ", userPermissions);
            }
        }
        return userPermissions;
    }

    private boolean isMemberInPermissionList(final JsonArray permissions, final List<ExpectedPermission> expectedPermissions) {
        boolean hasPermission = false;
        if (!expectedPermissions.isEmpty() && !permissions.isEmpty()) {

            final HashSet<String> expectedPermissionSet = expectedPermissions.stream()
                    .map(ExpectedPermission::getKey)
                    .collect(toCollection(HashSet::new));

            final HashSet<String> actualPermissionSet = permissions.stream()
                    .map(permission -> (JsonObject) permission)
                    .map(permission -> getKeyForPermission(permission))
                    .collect(toCollection(HashSet::new));
            hasPermission = expectedPermissionSet.stream()
                    .anyMatch(expectedPermissionKey -> actualPermissionSet.stream()
                            .anyMatch(actualPermissionKey -> expectedPermissionKey.contains(actualPermissionKey)));
            if (hasPermission) {
                return hasPermission;
            } else {
                final HashSet<String> expectedPermissionSetWithoutSource = expectedPermissions.stream()
                        .map(ExpectedPermission::getKeyWithOutSource)
                        .collect(toCollection(HashSet::new));
                return expectedPermissionSetWithoutSource.stream()
                        .anyMatch(expectedPermissionKey -> actualPermissionSet.stream()
                                .anyMatch(actualPermissionKey -> expectedPermissionKey.contains(actualPermissionKey)));
            }

        }

        return hasPermission;
    }

    private String getKeyForPermission(final JsonObject permissionJson) {
        StringBuffer stringBuffer = new StringBuffer("");
        final String strObject = permissionJson.getString("object", null);
        final String strAction = permissionJson.getString("action", null);
        final String strSource = permissionJson.getString("source", null);
        final String strTarget = permissionJson.getString("target", null);
        if (isNotEmpty(strObject)) {
            stringBuffer.append(strObject).append("_");
        }
        if (isNotEmpty(strAction)) {
            stringBuffer.append(strAction).append("_");
        }
        if (isNotEmpty(strSource)) {
            stringBuffer.append(strSource).append("_");
        }
        if (isNotEmpty(strTarget)) {
            stringBuffer.append(strTarget);
        }
        return StringUtils.stripEnd(stringBuffer.toString(), "_");
    }

    private boolean isMemberInGroups(JsonObject userGroupsJson, final List<String> suppliedGroupList) {
        boolean isMember = false;
        if (!suppliedGroupList.isEmpty() && userGroupsJson != null && !userGroupsJson.getJsonArray(GROUPS).isEmpty()) {
            isMember = userGroupsJson.getJsonArray(GROUPS).stream()
                    .map(groupJson -> (JsonObject) groupJson)
                    .anyMatch(groupJsonObj -> suppliedGroupList.contains(groupJsonObj.getString(GROUP_NAME, null)));
        }
        return isMember;
    }


}
