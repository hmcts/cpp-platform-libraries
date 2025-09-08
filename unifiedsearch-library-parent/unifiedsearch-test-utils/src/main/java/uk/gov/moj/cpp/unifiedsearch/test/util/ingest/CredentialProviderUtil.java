package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.apache.http.auth.AuthScope.ANY;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.ElasticSearchTestHostProvider.getAdminPassword;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.ElasticSearchTestHostProvider.getAdminUserName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.ElasticSearchTestHostProvider.getWriterName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.ElasticSearchTestHostProvider.getWriterPassword;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class CredentialProviderUtil {

    CredentialsProvider credentialsProvider() {

        return credentialsProvider(CRIME_CASE);
    }

    CredentialsProvider adminCredentialsProvider() {

        return adminCredentialsProvider(CRIME_CASE);
    }

    CredentialsProvider credentialsProvider(final IndexInfo indexInfo) {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(ANY,
                new UsernamePasswordCredentials(getWriterName(indexInfo.getWriterUserNameKey(), indexInfo.getDefaultWriterUserName()), getWriterPassword(indexInfo.getWriterPasswordKey(), indexInfo.getDefaultWriterPassword())));

        return credentialsProvider;
    }

    CredentialsProvider adminCredentialsProvider(final IndexInfo indexInfo) {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(ANY,
                new UsernamePasswordCredentials(getAdminUserName(indexInfo.getAdminUserNameKey(), indexInfo.getDefaultAdminUserName()), getAdminPassword(indexInfo.getAdminPasswordKey(), indexInfo.getDefaultAdminPassword())));

        return credentialsProvider;
    }
}
