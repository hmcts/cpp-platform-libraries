package uk.gov.moj.cpp.systemusers.exception;

/**
 * A named {@link RuntimeException} for errors that occur during PostConstruct of the System User
 * Provider.
 */
public class SystemUserProviderException extends RuntimeException {

    public SystemUserProviderException(final String string, final Throwable cause) {
        super(string, cause);
    }
}
