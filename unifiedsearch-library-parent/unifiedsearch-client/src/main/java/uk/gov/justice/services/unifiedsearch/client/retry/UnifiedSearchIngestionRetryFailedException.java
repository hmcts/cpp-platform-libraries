package uk.gov.justice.services.unifiedsearch.client.retry;

/**
 * Exception thrown when the retry interceptor fails to complete after reaching the maximum number
 * of retries.
 */
public class UnifiedSearchIngestionRetryFailedException extends RuntimeException {

    private static final long serialVersionUID = -3290663041510311855L;

    public UnifiedSearchIngestionRetryFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}