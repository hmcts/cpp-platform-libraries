package uk.gov.justice.services.unifiedsearch.client.transformer;

public class UnifiedSearchIndexTransformerException extends RuntimeException {

    public UnifiedSearchIndexTransformerException(final String message, final Throwable ex) {
        super(message, ex);
    }

    public UnifiedSearchIndexTransformerException(final String message) {
        super(message);
    }
}
