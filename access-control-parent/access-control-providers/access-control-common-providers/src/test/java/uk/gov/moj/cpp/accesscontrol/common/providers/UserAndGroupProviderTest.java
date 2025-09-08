package uk.gov.moj.cpp.accesscontrol.common.providers;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithDefaults;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;
import static uk.gov.moj.cpp.accesscontrol.common.providers.UserAndGroupProvider.GROUP_NAME_SYSTEM_USERS;
import static uk.gov.moj.cpp.accesscontrol.test.util.GroupsJsonBuilder.groupsJson;
import static uk.gov.moj.cpp.accesscontrol.test.util.PermissionJsonBuilder.permissionsJson;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.drools.ExpectedPermission;
import uk.gov.moj.cpp.accesscontrol.test.util.Group;
import uk.gov.moj.cpp.accesscontrol.test.util.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.json.JsonValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class UserAndGroupProviderTest {

    @Mock
    private Logger logger;

    @Mock
    private Requester requester;

    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapperProducer().objectMapper();

    @Spy
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;

    @InjectMocks
    private UserAndGroupProvider provider;

    @BeforeEach
    public void setup() {
        provider.enveloper = enveloper;
        setField(this.objectToJsonObjectConverter, "mapper", new ObjectMapperProducer().objectMapper());
    }

    @Test
    public void shouldReturnTrueIfThereIsAGroup() throws Exception {

        List<Group> userGroupsResponse = Collections.singletonList(new Group("groupId", "groupName"));
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(metadataWithDefaults(),
                groupsJson().withGroups(userGroupsResponse).build()));

        assertTrue(provider.isInGroup(defaultAction(), "group2"));
    }

    @Test
    public void shouldReturnFalseIfGroupsIsEmpty() throws Exception {

        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().build()));

        assertFalse(provider.isInGroup(defaultAction(), "group2"));
    }

    @Test
    public void shouldReturnFalseIfNotFound() throws Exception {
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(metadataWithDefaults(), JsonValue.NULL));

        assertFalse(provider.isInGroup(defaultAction(), "group3"));
    }

    @Test
    public void shouldReturnFalseIfActionDoesNotContainUserId() {
        assertThat(provider.isInGroup(new Action(envelope().with(metadataOf(randomUUID(), "dummy")).build()), "notUsed"), is(false));
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenUserIdNotPresent() {
        List<Group> userGroupsResponse = Collections.singletonList(new Group(randomUUID().toString(), "Group3"));

        Action actionWithNoUser = new Action(envelope().with(metadataOf(randomUUID(), "dummy")).build());
        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(actionWithNoUser, "Group1", "Group2", "Group3", "Group4");

        assertThat(result, is(false));
        verify(requester, times(0)).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenNullUserGroupsResponse() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), JsonValue.NULL));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), "Group1", "Group2", "Group3", "Group4");

        assertThat(result, is(false));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenEmptyUserGroupsResponse() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().build()));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), "Group1", "Group2", "Group3", "Group4");

        assertThat(result, is(false));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenNoGroupNameInUserGroupsResponse() {
        List<Group> userGroupsResponse = Collections.singletonList(new Group(randomUUID().toString(), null));
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), "Group1", "Group2", "Group3", "Group4");

        assertThat(result, is(false));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenUserExistInGroups() {
        List<Group> userGroupsResponse = Collections.singletonList(new Group(randomUUID().toString(), "Group3"));
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), "Group1", "Group2", "Group3", "Group4");

        assertThat(result, is(true));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroupsList_WhenUserExistInGroups() {
        List<Group> userGroupsResponse = Collections.singletonList(new Group(randomUUID().toString(), "Group3"));
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), Arrays.asList("Group1", "Group2", "Group3", "Group4"));

        assertThat(result, is(true));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void testIsMemberOfAnyOfTheSuppliedGroups_WhenUserDoesNotExistInGroups() {
        Group systemUserGroup = new Group(randomUUID().toString(), GROUP_NAME_SYSTEM_USERS);
        Group anyGroup = new Group(randomUUID().toString(), GROUP_NAME_SYSTEM_USERS);
        List<Group> userGroupsResponse = Arrays.asList(systemUserGroup, anyGroup);
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        String[] suppliedGroupNames = new String[]{"Group1", "Group2", "Group3", "Group4", "Group5", "Group6"};
        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), suppliedGroupNames);

        assertThat(result, is(false));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void shouldReturnTrueIfUserIsSystemUser() {
        Group systemUserGroup = new Group(randomUUID().toString(), GROUP_NAME_SYSTEM_USERS);
        Group anyGroup = new Group(randomUUID().toString(), GROUP_NAME_SYSTEM_USERS);
        List<Group> userGroupsResponse = Arrays.asList(systemUserGroup, anyGroup);
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        boolean result = provider.isSystemUser(defaultAction());

        assertThat(result, is(true));
        verify(requester).requestAsAdmin(any());
    }

    @Test
    public void shouldReturnFalseIfUserIsNotSystemUser() {
        Group chargingLawyerGroup = new Group(randomUUID().toString(), "Charging Lawyers");
        List<Group> userGroupsResponse = Arrays.asList(chargingLawyerGroup);
        when(requester.requestAsAdmin(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), groupsJson().withGroups(userGroupsResponse).build()));

        boolean result = provider.isMemberOfAnyOfTheSuppliedGroups(defaultAction(), GROUP_NAME_SYSTEM_USERS);

        assertThat(result, is(false));
        verify(requester, times(1)).requestAsAdmin(any());
    }

    @Test
    public void givenUserhasGenericPermisssion_whenExpectedPermissionIsSame_thenExpectTrue() {
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                      .withObject("Case")
                      .withAction("View")
                       .build() );
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .build();
        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();
        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(true));
        verify(requester).request(any());
    }

    @Test
    public void givenUserhasSpecificPermisssion_whenExpectedPermissionIsSame_thenExpectTrue() {
        final String caseId = randomUUID().toString();
        final String userId = "userId123";
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                            .withObject("Case")
                            .withAction("View")
                            .withSource(userId)
                            .withTarget(caseId)
                            .build());
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId)
                .withTarget(caseId)
                .build();
        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();


        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(true));
        verify(requester).request(any());
    }

    @Test
    public void givenUserhasSpecificPermisssion_whenExpectedPermissionIsNotSame_thenExpectFalse() {
        final String caseId = randomUUID().toString();
        final String userId1 = "userId123";
        final String userId2 = "userId345";
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId1)
                .withTarget(caseId)
                .build());
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId2)
                .withTarget(caseId)
                .build();
        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();
        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(false));
        verify(requester).request(any());
    }

    @Test
    public void givenUserHasSpecificPermission_whenExpectedPermissionIsPartOfActualPermissionWithSource_thenExpectTrue (){
        final String caseId = randomUUID().toString();
        final String userId1 = "userId123";
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId1)
                .build());
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId1)
                .withTarget(caseId)
                .build();
        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();

        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(true));
        verify(requester).request(any());
    }

    @Test
    public void givenUserHasSpecificPermission_whenExpectedPermissionIsPartOfActualPermissionWithTarget_thenExpectTrue (){
        final String caseId = randomUUID().toString();
        final String userId1 = "userId123";
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                .withObject("Case")
                .withAction("View")
                .withTarget(caseId)
                .build());
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .withSource(userId1)
                .withTarget(caseId)
                .build();

        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();
        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(true));
        verify(requester).request(any());
    }

    @Test
    public void givenUserHasSpecificPermissionWithTarget_whenExpectedPermissionIsPartOfActualPermissionWithTarget_thenExpectTrue (){
        final String caseId = randomUUID().toString();
        List<Permission> actualPermissionList = Collections.singletonList(Permission.builder()
                .withObject("Case")
                .withAction("View")
                .withTarget(caseId)
                .build());
        when(requester.request(any(JsonEnvelope.class)))
                .thenReturn(envelopeFrom(metadataWithDefaults(), permissionsJson().withPermissions(actualPermissionList).build()));
        ExpectedPermission expectedPermission = ExpectedPermission.builder()
                .withObject("Case")
                .withAction("View")
                .withTarget(caseId)
                .build();
        final String strExpectedPermission = objectToJsonObjectConverter.convert(expectedPermission).toString();

        boolean result = provider.hasPermission(defaultAction(), strExpectedPermission);

        assertThat(result, is(true));
        verify(requester).request(any());
    }

    private Action defaultAction() {
        return new Action(envelope().with(metadataOf(randomUUID(), "dummy").withUserId("userId123")).build());
    }
}
