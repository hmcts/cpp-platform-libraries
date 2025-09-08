package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory.createEnveloper;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.metadata;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUIDAndName;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import java.util.UUID;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class SjpProviderTest {

    private static final String CASE_ID_KEY = "caseId";
    private static final String ATTRIBUTE_PROSECUTING_AUTHORITY = "prosecutingAuthority";
    private static final String QUERY_CASE_PROSECUTING_AUTHORITY_ID = "sjp.query.case-prosecuting-authority";
    private static final String PROSECUTING_AUTHORITY = "TVL";

    private UUID caseId;

    @BeforeEach
    public void init() {
        caseId = randomUUID();
    }

    @Mock
    private Requester requester;

    @Mock
    private ProsecutingAuthorityProvider prosecutingAuthorityProvider;

    @Spy
    private Enveloper enveloper = createEnveloper();

    @InjectMocks
    private SjpProvider sjpProvider;

    @Test
    public void shouldReturnTrueIfUserHasProsecutingAuthorityToCase() {
        when(requester.requestAsAdmin(
                argThat(getCaseProsecutingAuthorityQueryMatcher())))
                .thenReturn(buildSjpResponse(PROSECUTING_AUTHORITY));

        when(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(any(), eq(PROSECUTING_AUTHORITY))).thenReturn(true);

        boolean result = sjpProvider.hasProsecutingAuthorityToCase(buildAction(CASE_ID_KEY, caseId.toString()));

        assertThat(result, is(true));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotHaveProsecutingAuthorityToCase() {
        when(requester.requestAsAdmin(
                argThat(getCaseProsecutingAuthorityQueryMatcher())))
                .thenReturn(buildSjpResponse(PROSECUTING_AUTHORITY));

        when(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(any(), eq(PROSECUTING_AUTHORITY))).thenReturn(false);

        boolean result = sjpProvider.hasProsecutingAuthorityToCase(buildAction(CASE_ID_KEY, caseId.toString()));

        assertThat(result, is(false));
    }

    @Test
    public void shouldReturnTrueIfCaseDoesNotExistOrDoesNotHaveProsecutingAuthority() {
        when(requester.requestAsAdmin(
                argThat(getCaseProsecutingAuthorityQueryMatcher())))
                .thenReturn(envelope().withNullPayload().build());

        boolean result = sjpProvider.hasProsecutingAuthorityToCase(buildAction(CASE_ID_KEY, caseId.toString()));

        assertThat(result, is(true));
    }


    private Matcher<JsonEnvelope> getCaseProsecutingAuthorityQueryMatcher() {
        return jsonEnvelope(metadata().withName(QUERY_CASE_PROSECUTING_AUTHORITY_ID),
                payload().isJson(withJsonPath("$.caseId", equalTo(caseId.toString()))));
    }

    private JsonEnvelope buildSjpResponse(final String prosecutingAuthority) {
        return envelope().withPayloadOf(prosecutingAuthority, ATTRIBUTE_PROSECUTING_AUTHORITY).build();
    }

    private Action buildAction(final String key, final String value) {
        return new Action(envelope().with(metadataWithRandomUUIDAndName()).withPayloadOf(value, key).build());
    }
}