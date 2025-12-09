package uk.gov.moj.cpp.accesscontrol.hearing.providers;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import javax.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory.createEnveloper;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.metadata;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUIDAndName;


@ExtendWith(MockitoExtension.class)
public class HearingProviderTest {
    private static final String HEARING = "hearing";
    private static final String ID_KEY = "id";
    private static final String USER_ID_KEY = "userId";
    private static final String HEARING_ID_KEY = "hearingId";
    private static final String APPROVAL_REQUESTED = "approvalsRequested";
    private static final String MATERIAL_ID_KEY = "materialId";
    private static final String LISTING_OFFICER = "Listing Officer";
    private static final String COURT_CLERK = "Court Clerk";
    private static final String ALLOWED_USER_GROUPS = "allowedUserGroups";
    private static final String HEARING_QUERY_SEARCH_BY_MATERIAL_ID = "hearing.query.search-by-material-id";
    private static final String HEARING_QUERY_SEARCH_BY_HEARING_ID = "hearing.get.hearing";

    private UUID materialId;
    private UUID hearingId;
    private UUID userId;

    @BeforeEach
    public void init() {
        materialId = randomUUID();
        userId = randomUUID();
        hearingId = randomUUID();
    }

    @Mock
    private Requester requester;

    @Spy
    private Enveloper enveloper = createEnveloper();

    @InjectMocks
    private HearingProvider hearingProvider;

    @Test
    public void shouldReturnAllowedUserGroupsForMaterialId() {
        when(requester.requestAsAdmin(argThat(getGroupsByMaterialIdQueryMatcher(materialId)))).thenReturn(buildHearingResponse());
        List<String> result = hearingProvider.getAllowedUserGroups(buildAction(MATERIAL_ID_KEY, materialId.toString()));
        assertThat(result, is(Arrays.asList(LISTING_OFFICER, COURT_CLERK)));
    }

    @Test
    public void shouldConfirmThatUserIsAllowedToApproveAnResult() {
        when(requester.requestAsAdmin(argThat(getApprovalRequetsByHearingIdQueryMatcher(hearingId, userId)))).thenReturn(buildUserIsAllowedToApproveAnResult(randomUUID().toString(), hearingId.toString()));
        boolean result = hearingProvider.isUserAllowedToApproveResultAmendment(buildActionForHearing(hearingId.toString(), userId.toString()));
        assertThat(result, is(true));
    }

    @Test
    public void shouldConfirmThatUserIsNotAllowedToApproveAnResult() {
        when(requester.requestAsAdmin(argThat(getApprovalRequetsByHearingIdQueryMatcher(hearingId, userId)))).thenReturn(buildUserIsAllowedToApproveAnResult(userId.toString(), hearingId.toString()));
        boolean result = hearingProvider.isUserAllowedToApproveResultAmendment(buildActionForHearing(hearingId.toString(), userId.toString()));
        assertThat(result, is(false));
    }

    private Matcher<JsonEnvelope> getGroupsByMaterialIdQueryMatcher(final UUID materialId) {
        return jsonEnvelope(metadata().withName(HEARING_QUERY_SEARCH_BY_MATERIAL_ID),
                payload().isJson(withJsonPath("$.q", equalTo(materialId.toString()))));
    }

    private JsonEnvelope buildHearingResponse() {
        String[] strings = {LISTING_OFFICER, COURT_CLERK};
        return envelope().withPayloadOf(strings, ALLOWED_USER_GROUPS).build();
    }

    private Matcher<JsonEnvelope> getApprovalRequetsByHearingIdQueryMatcher(final UUID hearingId, final UUID userId) {
        return jsonEnvelope(metadata().withName(HEARING_QUERY_SEARCH_BY_HEARING_ID),
                payload().isJson(allOf(
                        withJsonPath("$.hearingId", equalTo(hearingId.toString()))
                )));
    }

    private JsonEnvelope buildUserIsAllowedToApproveAnResult(final String userId, final String hearingId) {
        final JsonObject response =
                getJsonBuilderFactory().createObjectBuilder()
                        .add(APPROVAL_REQUESTED,
                                getJsonBuilderFactory().createArrayBuilder()
                                        .add(getJsonBuilderFactory().createObjectBuilder()
                                                .add(USER_ID_KEY, userId)
                                                .add(HEARING_ID_KEY, hearingId)
                                                .build())
                                        .build())
                        .build();
        return envelope().withPayloadOf(response, HEARING).build();
    }

    private Action buildAction(final String key, final String value) {
        return new Action(envelope().with(metadataWithRandomUUIDAndName())
                .withPayloadOf(value, key).build());
    }

    private Action buildActionForHearing(final String hearingId, final String userId) {
        return new Action(envelope().with(metadataWithRandomUUIDAndName())
                .withPayloadOf(hearingId, ID_KEY)
                .withPayloadOf(userId, USER_ID_KEY)
                .build());
    }
}