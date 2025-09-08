package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class CredentialProviderUtilTest {

    @InjectMocks
    private CredentialProviderUtil credentialProviderUtil;

    private static Stream<Arguments> provideCaseIndex() {
        return Stream.of(
                Arguments.of(CRIME_CASE, CPS_CASE)
        );
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateCredentialsProvider() {

        final CredentialsProvider credentialsProvider1 = credentialProviderUtil.credentialsProvider();
        final CredentialsProvider credentialsProvider2 = credentialProviderUtil.adminCredentialsProvider();

        assertNotNull(credentialsProvider1);
        assertNotNull(credentialsProvider2);
    }

    @ParameterizedTest
    @MethodSource("provideCaseIndex")
    public void shouldCreateCredentialsProviderForCpsSearch(final IndexInfo indexInfo) {
        final CredentialsProvider writerCredentialsProvider = credentialProviderUtil.credentialsProvider(indexInfo);
        final CredentialsProvider adminCredentialsProvider = credentialProviderUtil.adminCredentialsProvider(indexInfo);

        assertCredentialsProvider(writerCredentialsProvider, adminCredentialsProvider, indexInfo);
    }

    private void assertCredentialsProvider(final CredentialsProvider writerCredentialsProvider, final CredentialsProvider adminCredentialsProvider, final IndexInfo indexInfo) {
        assertNotNull(writerCredentialsProvider);
        assertThat(writerCredentialsProvider.getCredentials(AuthScope.ANY).getPassword(), is(indexInfo.getDefaultWriterPassword()));
        assertThat(writerCredentialsProvider.getCredentials(AuthScope.ANY).getUserPrincipal().getName(), is(indexInfo.getDefaultWriterUserName()));

        assertNotNull(adminCredentialsProvider);
        assertThat(adminCredentialsProvider.getCredentials(AuthScope.ANY).getPassword(), is(indexInfo.getDefaultAdminPassword()));
        assertThat(adminCredentialsProvider.getCredentials(AuthScope.ANY).getUserPrincipal().getName(), is(indexInfo.getDefaultAdminUserName()));
    }
}