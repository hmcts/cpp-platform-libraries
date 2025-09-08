package uk.gov.moj.cpp.platform.azure.utils.ssl;

import static java.lang.String.format;
import static java.util.Collections.list;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextFactory {
    //See : https://docs.microsoft.com/en-us/azure/app-service/configure-ssl-certificate-in-code
    private static final String AZURE_WINDOWS_KEYSTORE_NAME = "Windows-MY";
    private static final String TLS = "TLSv1.2";

    public SSLContext createForWindows(final Logger logger) {
        return create(logger, AZURE_WINDOWS_KEYSTORE_NAME);
    }

    protected SSLContext create(final Logger logger, final String keystoreTypeName) {

        try {
            logger.info("In  SSLContextBuilder.build");

            final KeyStore keyStore = KeyStore.getInstance(keystoreTypeName);
            keyStore.load(null, null);

            final SSLContext sslContext = SSLContext.getInstance(TLS);

            final List<String> aliases = list(keyStore.aliases());
            if (logger.isLoggable(INFO)) {
                logger.info(format("KeyStore contains certs that will be used in SSLContext: %s", aliases));
            }

            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            logger.info("SSLContext has been build successfully");

            return sslContext;
        } catch (IOException | GeneralSecurityException e) {
            logger.log(SEVERE, e, () -> "Error ");
        }

        return null;
    }


}
