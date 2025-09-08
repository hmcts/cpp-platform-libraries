package uk.gov.moj.cpp.accesscontrol.assignment.providers;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithDefaults;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;
import uk.gov.moj.cpp.accesscontrol.assignment.providers.util.AssignmentQueryJsonBuilder;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class AssignmentProviderTest {

    private static final String USER_ID_MATCH = "userId123";
    private static final String USER_ID_NO_MATCH = "no-match";

    private static final String REVIEW_ID = randomUUID().toString();

    @Mock
    private Logger logger;

    @Mock
    private Requester requester;

    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @InjectMocks
    private AssignmentProvider provider;

    @BeforeEach
    public void setup() {
        provider.enveloper = enveloper;
    }

    @Test
    public void shouldMatch() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(
                metadataWithDefaults().withUserId(USER_ID_MATCH), AssignmentQueryJsonBuilder.getReviewJson()
                        .withAssignee(USER_ID_MATCH).build()));
        assertThat(provider.isUserAssignedByReviewId(getAction(USER_ID_MATCH)), is(true));
    }

    @Test
    public void shouldNotMatch() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(
                metadataWithDefaults().withUserId(USER_ID_NO_MATCH), AssignmentQueryJsonBuilder.getReviewJson()
                        .withAssignee(USER_ID_MATCH).build()));
        assertThat(provider.isUserAssignedByReviewId(getAction(USER_ID_NO_MATCH)), is(false));
    }

    @Test
    public void shouldMatchWithReviewId() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(
                metadataWithDefaults().withUserId(USER_ID_MATCH), AssignmentQueryJsonBuilder.getReviewJson()
                        .withAssignee(USER_ID_MATCH).build()));

        assertThat(provider.isUserAssignedByReviewId(getActionWithNoPayload(USER_ID_MATCH), "reviewId"), is(true));
    }

    @Test
    public void shouldNotMatchWithReviewId() {
        when(requester.requestAsAdmin(any(JsonEnvelope.class))).thenReturn(envelopeFrom(
                metadataWithDefaults().withUserId(USER_ID_NO_MATCH), AssignmentQueryJsonBuilder.getReviewJson()
                        .withAssignee(USER_ID_MATCH).build()));

        assertThat(provider.isUserAssignedByReviewId(getActionWithNoPayload(USER_ID_NO_MATCH), "reviewId"), is(false));
    }

    private Action getAction(final String userId) {
        return new Action(envelope().with(metadataOf(randomUUID(), "dummy")
                .withUserId(userId))
                .withPayloadOf(REVIEW_ID, "reviewId")
                .build());
    }

    private Action getActionWithNoPayload(final String userId) {
        return new Action(envelope().with(metadataOf(randomUUID(), "dummy")
                .withUserId(userId))
                .build());
    }
}