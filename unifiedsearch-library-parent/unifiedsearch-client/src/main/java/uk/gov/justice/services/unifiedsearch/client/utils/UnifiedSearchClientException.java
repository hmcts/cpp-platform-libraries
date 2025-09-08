package uk.gov.justice.services.unifiedsearch.client.utils;

public class UnifiedSearchClientException extends RuntimeException {

    public UnifiedSearchClientException(final String message, final Throwable ex) {
        super(message, ex);
    }

    public UnifiedSearchClientException(final String message) {
        super(message);
    }
}