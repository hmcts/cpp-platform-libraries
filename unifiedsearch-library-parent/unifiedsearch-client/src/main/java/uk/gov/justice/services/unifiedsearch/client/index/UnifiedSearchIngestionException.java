package uk.gov.justice.services.unifiedsearch.client.index;

public class UnifiedSearchIngestionException extends RuntimeException {

    public UnifiedSearchIngestionException(final String message, final Throwable ex) {
        super(message, ex);
    }

    public UnifiedSearchIngestionException(final String message) {
        super(message);
    }
}