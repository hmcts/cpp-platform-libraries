package uk.gov.justice.services.unifiedsearch.healthchecks;

public class ElasticSearchHealtcheckQueryException extends RuntimeException {

    public ElasticSearchHealtcheckQueryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
