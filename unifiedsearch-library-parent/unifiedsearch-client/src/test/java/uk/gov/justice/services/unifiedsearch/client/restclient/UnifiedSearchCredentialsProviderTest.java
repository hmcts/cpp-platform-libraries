package uk.gov.justice.services.unifiedsearch.client.restclient;

import static org.apache.http.auth.AuthScope.ANY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_ES_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_ES_READ_USER_PASSWORD;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_ES_WRITE_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_ES_WRITE_USER_PASSWORD;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_WRITE_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_MONITOR_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_MONITOR_USER_PASSWORD;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_READ_USER_PASSWORD;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_WRITE_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.ES_WRITE_USER_PASSWORD;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import uk.gov.justice.services.common.configuration.GlobalValue;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.inject.Inject;

import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnifiedSearchCredentialsProviderTest {

    private UnifiedSearchCredentialsProvider unifiedSearchCredentialsProvider = new UnifiedSearchCredentialsProvider();

    @BeforeEach
    public void setup() {
        setField(unifiedSearchCredentialsProvider, "elasticsearchIndexWriterUser", ES_WRITE_USER);
        setField(unifiedSearchCredentialsProvider, "elasticsearchIndexWriterPassword", ES_WRITE_USER_PASSWORD);

        setField(unifiedSearchCredentialsProvider, "elasticsearchIndexReaderUser", ES_READ_USER);
        setField(unifiedSearchCredentialsProvider, "elasticsearchIndexReaderPassword", ES_READ_USER_PASSWORD);

        setField(unifiedSearchCredentialsProvider, "cpssearchIndexWriterUser", CPS_ES_WRITE_USER);
        setField(unifiedSearchCredentialsProvider, "cpssearchIndexWriterPassword", CPS_ES_WRITE_USER_PASSWORD);

        setField(unifiedSearchCredentialsProvider, "cpssearchIndexReaderUser", CPS_ES_READ_USER);
        setField(unifiedSearchCredentialsProvider, "cpssearchIndexReaderPassword", CPS_ES_READ_USER_PASSWORD);

        setField(unifiedSearchCredentialsProvider, "elasticsearchMonitorUser", ES_MONITOR_USER);
        setField(unifiedSearchCredentialsProvider, "elasticsearchMonitorUserPassword", ES_MONITOR_USER_PASSWORD);
    }

    @Test
    public void shouldProduceWriteCredentialsProvider() {
        final CredentialsProvider writeCredentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(WRITE_USER);

        final Credentials writeCredentials = writeCredentialsProvider.getCredentials(ANY);

        assertThat(writeCredentialsProvider, instanceOf(CredentialsProvider.class));
        assertThat(writeCredentials.getUserPrincipal().getName(), is(ES_WRITE_USER));
        assertThat(writeCredentials.getPassword(), is(ES_WRITE_USER_PASSWORD));
    }

    @Test
    public void shouldProduceReadCredentialsProvider() {
        final CredentialsProvider readCredentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(READ_USER);

        final Credentials readCredentials = readCredentialsProvider.getCredentials(ANY);

        assertThat(readCredentialsProvider, instanceOf(CredentialsProvider.class));
        assertThat(readCredentials.getUserPrincipal().getName(), is(ES_READ_USER));
        assertThat(readCredentials.getPassword(), is(ES_READ_USER_PASSWORD));
    }

    @Test
    public void shouldProduceMonitorCredentialsProvider() {
        final CredentialsProvider readCredentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(MONITOR_USER);

        final Credentials readCredentials = readCredentialsProvider.getCredentials(ANY);

        assertThat(readCredentialsProvider, instanceOf(CredentialsProvider.class));
        assertThat(readCredentials.getUserPrincipal().getName(), is(ES_MONITOR_USER));
        assertThat(readCredentials.getPassword(), is(ES_MONITOR_USER_PASSWORD));
    }

    @Test
    public void shouldThrowExceptionForUnknownUser() {


        final UnifiedSearchClientException unifiedSearchClientException = assertThrows(
                UnifiedSearchClientException.class,
                () -> unifiedSearchCredentialsProvider.getCredentialsProvider("Unknown User"));

        assertThat(unifiedSearchClientException.getMessage(), is("Unknown unifiedsearch user type: Unknown User"));
    }

    @Test
    public void shouldProduceCPSWriteCredentialsProvider() {
        final CredentialsProvider writeCredentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(CPS_WRITE_USER);

        final Credentials writeCredentials = writeCredentialsProvider.getCredentials(ANY);

        assertThat(writeCredentialsProvider, instanceOf(CredentialsProvider.class));
        assertThat(writeCredentials.getUserPrincipal().getName(), is(CPS_ES_WRITE_USER));
        assertThat(writeCredentials.getPassword(), is(CPS_ES_WRITE_USER_PASSWORD));
    }

    @Test
    public void shouldProduceCPSReadCredentialsProvider() {
        final CredentialsProvider readCredentialsProvider = unifiedSearchCredentialsProvider.getCredentialsProvider(CPS_READ_USER);

        final Credentials readCredentials = readCredentialsProvider.getCredentials(ANY);

        assertThat(readCredentialsProvider, instanceOf(CredentialsProvider.class));
        assertThat(readCredentials.getUserPrincipal().getName(), is(CPS_ES_READ_USER));
        assertThat(readCredentials.getPassword(), is(CPS_ES_READ_USER_PASSWORD));
    }
}
