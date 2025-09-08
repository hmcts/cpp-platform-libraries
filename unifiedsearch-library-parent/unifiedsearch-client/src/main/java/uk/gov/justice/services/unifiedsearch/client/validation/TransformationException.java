package uk.gov.justice.services.unifiedsearch.client.validation;

import java.util.List;

public class TransformationException extends RuntimeException {

    private List<String> validationFailures;

    public TransformationException(final String message, final List<String> validationFailures, final Throwable cause) {
        super(message, cause);
        this.validationFailures = validationFailures;
    }

    public TransformationException(final String message) {
        super(message);
    }

    public TransformationException(final String message, final Throwable cause) {
        super(message, cause);
    }


    public List<String> getValidationFailures() {
        return validationFailures;
    }
}
