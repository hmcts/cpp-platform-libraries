package uk.gov.justice.services.unifiedsearch.client.restclient;


import static java.lang.String.format;
import static org.apache.http.auth.AuthScope.ANY;
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
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

@ApplicationScoped
public class UnifiedSearchCredentialsProvider {

    @Inject
    @GlobalValue(key = "elasticsearchIndexWriterUser", defaultValue = ES_WRITE_USER)
    private String elasticsearchIndexWriterUser;

    @Inject
    @GlobalValue(key = "elasticsearchIndexWriterPassword", defaultValue = ES_WRITE_USER_PASSWORD)
    private String elasticsearchIndexWriterPassword;

    @Inject
    @GlobalValue(key = "elasticsearchIndexReaderUser", defaultValue = ES_READ_USER)
    private String elasticsearchIndexReaderUser;

    @Inject
    @GlobalValue(key = "elasticsearchIndexReaderPassword", defaultValue = ES_READ_USER_PASSWORD)
    private String elasticsearchIndexReaderPassword;

    @Inject
    @GlobalValue(key = "elasticsearchMonitorUser", defaultValue = ES_MONITOR_USER)
    private String elasticsearchMonitorUser;

    @Inject
    @GlobalValue(key = "elasticsearchMonitorUserPassword", defaultValue = ES_MONITOR_USER_PASSWORD)
    private String elasticsearchMonitorUserPassword;

    @Inject
    @GlobalValue(key = "cpssearchIndexWriterUser", defaultValue = CPS_ES_WRITE_USER)
    private String cpssearchIndexWriterUser;

    @Inject
    @GlobalValue(key = "cpssearchIndexWriterPassword", defaultValue = CPS_ES_WRITE_USER_PASSWORD)
    private String cpssearchIndexWriterPassword;

    @Inject
    @GlobalValue(key = "cpssearchIndexReaderUser", defaultValue = CPS_ES_READ_USER)
    private String cpssearchIndexReaderUser;

    @Inject
    @GlobalValue(key = "cpssearchIndexReaderPassword", defaultValue = CPS_ES_READ_USER_PASSWORD)
    private String cpssearchIndexReaderPassword;

    public CredentialsProvider getCredentialsProvider(final String userType) {

        if (userType.equals(READ_USER)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(ANY, new UsernamePasswordCredentials(elasticsearchIndexReaderUser, elasticsearchIndexReaderPassword));
            return credentialsProvider;
        }
        if (userType.equals(WRITE_USER)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(ANY, new UsernamePasswordCredentials(elasticsearchIndexWriterUser, elasticsearchIndexWriterPassword));
            return credentialsProvider;
        }

        if (userType.equals(MONITOR_USER)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    elasticsearchMonitorUser,
                    elasticsearchMonitorUserPassword);
            credentialsProvider.setCredentials(ANY, credentials);

            return credentialsProvider;
        }

        if (userType.equals(CPS_READ_USER)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(ANY, new UsernamePasswordCredentials(cpssearchIndexReaderUser, cpssearchIndexReaderPassword));
            return credentialsProvider;
        }
        if (userType.equals(CPS_WRITE_USER)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(ANY, new UsernamePasswordCredentials(cpssearchIndexWriterUser, cpssearchIndexWriterPassword));
            return credentialsProvider;
        }

        throw new UnifiedSearchClientException(format("Unknown unifiedsearch user type: %s", userType));
    }
}
