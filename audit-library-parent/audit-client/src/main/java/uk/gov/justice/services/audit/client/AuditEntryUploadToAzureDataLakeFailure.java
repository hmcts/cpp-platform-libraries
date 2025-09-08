package uk.gov.justice.services.audit.client;

public class AuditEntryUploadToAzureDataLakeFailure extends RuntimeException {
    public AuditEntryUploadToAzureDataLakeFailure(final String message, final Throwable cause) {
        super(message, cause);
    }
}
