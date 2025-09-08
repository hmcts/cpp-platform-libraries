package uk.gov.moj.cpp.platform.azure.utils.ssl;

import static java.security.KeyStore.getDefaultType;
import static java.util.logging.Logger.getGlobal;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import javax.net.ssl.SSLContext;

import org.junit.jupiter.api.Test;

public class SSLContextFactoryTest {

    @Test
    public void shouldCreate() {
        final SSLContext sslContext = new SSLContextFactory().create(getGlobal(), getDefaultType());

        assertThat(sslContext, is(not((nullValue()))));
        assertThat(sslContext.getProtocol(), is("TLSv1.2"));
    }
}