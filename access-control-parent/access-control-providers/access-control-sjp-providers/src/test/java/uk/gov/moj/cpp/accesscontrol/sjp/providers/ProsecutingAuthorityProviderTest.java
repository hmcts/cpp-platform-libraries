package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.moj.cpp.accesscontrol.sjp.providers.ProsecutingAuthorityAccess.ALL_PROSECUTING_AUTHORITIES;
import static uk.gov.moj.cpp.accesscontrol.sjp.providers.ProsecutingAuthorityProvider.ACCESS_CONTROL_DISABLED_PROPERTY;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.spi.DefaultJsonMetadata;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;
import uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class ProsecutingAuthorityProviderTest {

    private static final String PROSECUTING_AUTHORITY = "PROC1";

    private static final String ACTION_NAME = "action-name";

    private final JsonEnvelope callingEnvelope = envelope()
            .with(DefaultJsonMetadata.metadataBuilder()
                    .withId(randomUUID())
                    .withUserId(randomUUID().toString())
                    .withName(ACTION_NAME)).build();

    @InjectMocks
    private ProsecutingAuthorityProvider prosecutingAuthorityProvider;

    @Spy
    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @Mock
    private Requester requester;

    @Mock
    private Logger logger;

    @AfterEach
    public void resetSystemProperty() {
        System.clearProperty(ACCESS_CONTROL_DISABLED_PROPERTY);
    }

    @Test
    public void shouldReturnNoProsecutingAuthorityAccess() {

        givenUserHasNoProsecutingAuthorityAccess();

        assertThat(prosecutingAuthorityProvider.getCurrentUsersProsecutingAuthorityAccess(callingEnvelope),
                is(ProsecutingAuthorityAccess.NONE));

        assertLogStatement();
    }

    @Test
    public void shouldReturnProsecutingAuthorityForSingleProsecutingAuthorityAccess() {

        givenUserHasProsecutingAuthorityAccess(PROSECUTING_AUTHORITY);

        ProsecutingAuthorityAccess currentUsersProsecutingAuthorityAccess = prosecutingAuthorityProvider.getCurrentUsersProsecutingAuthorityAccess(callingEnvelope);

        assertThat(currentUsersProsecutingAuthorityAccess, not(ProsecutingAuthorityAccess.NONE));
        assertThat(currentUsersProsecutingAuthorityAccess, not(ProsecutingAuthorityAccess.ALL));
        assertThat(currentUsersProsecutingAuthorityAccess.getProsecutingAuthority(), is(PROSECUTING_AUTHORITY));

        assertLogStatement();
    }

    @Test
    public void shouldReturnAllProsecutingAuthorityAccessScope() {

        givenUserHasProsecutingAuthorityAccess(ALL_PROSECUTING_AUTHORITIES);

        assertThat(prosecutingAuthorityProvider.getCurrentUsersProsecutingAuthorityAccess(callingEnvelope),
                is(ProsecutingAuthorityAccess.ALL));

        assertLogStatement();
    }

    @Test
    public void shouldReturnTrueIfUserHasAllProsecutingAuthorityAccess() {

        givenUserHasProsecutingAuthorityAccess(ALL_PROSECUTING_AUTHORITIES);

        assertThat(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(
                callingEnvelope, PROSECUTING_AUTHORITY), is(true));

        assertLogStatement();
    }

    @Test
    public void shouldReturnTrueIfUserHasSingleProsecutingAuthorityAccess() {

        givenUserHasProsecutingAuthorityAccess(PROSECUTING_AUTHORITY);

        assertThat(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(
                callingEnvelope, PROSECUTING_AUTHORITY), is(true));

        assertLogStatement();
    }

    @Test
    public void shouldReturnFalseIfUserHasSingleProsecutingAuthorityAccessForOther() {

        givenUserHasProsecutingAuthorityAccess("ANOTHER_TEST");

        assertThat(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(
                callingEnvelope, PROSECUTING_AUTHORITY), is(false));

        assertLogStatement();
    }

    @Test
    public void shouldReturnFalseIfUserHasNoProsecutingAuthorityAccess() {

        givenUserHasNoProsecutingAuthorityAccess();

        assertThat(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(
                callingEnvelope, PROSECUTING_AUTHORITY), is(false));

        assertLogStatement();
    }

    @Test
    public void shouldIgnoreAccessControlIfTheAccessControlDisabledPropertyIsTrue() {

        System.setProperty(ACCESS_CONTROL_DISABLED_PROPERTY, "true");

        assertThat(prosecutingAuthorityProvider.getCurrentUsersProsecutingAuthorityAccess(callingEnvelope),
                is(ProsecutingAuthorityAccess.ALL));
        verifyNoInteractions(requester);
        verify(logger).trace("Skipping prosecuting authority access control due to configuration");
    }

    @Test
    public void shouldIgnoreAccessControlIfTheAccessControlDisabledPropertyIsTrueForCheck() {

        System.setProperty(ACCESS_CONTROL_DISABLED_PROPERTY, "true");

        assertThat(prosecutingAuthorityProvider.userHasProsecutingAuthorityAccess(
                callingEnvelope, PROSECUTING_AUTHORITY), is(true));

        verifyNoInteractions(requester);
        verify(logger).trace("Skipping prosecuting authority access control due to configuration");
    }

    private void givenUserHasNoProsecutingAuthorityAccess() {
        final JsonEnvelope build = JsonEnvelopeBuilder.envelope()
                .withPayloadOf("Fred Bloggs", "name")
                .build();
        when(requester.requestAsAdmin(any())).thenReturn(build);
    }

    private void givenUserHasProsecutingAuthorityAccess(final String prosecutingAuthorityAccess) {
        when(requester.requestAsAdmin(any())).thenReturn(userDetailsResponse(prosecutingAuthorityAccess));
    }

    private JsonEnvelope userDetailsResponse(final String prosecutingAuthorityAccess) {
        return envelope().withPayloadOf(prosecutingAuthorityAccess, "prosecutingAuthorityAccess").build();
    }

    private void assertLogStatement() {
        verify(logger).trace("Performing prosecuting authority access control for action: {}", ACTION_NAME);
    }
}
