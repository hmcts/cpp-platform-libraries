package uk.gov.justice.services.audit.client;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AuditEntryUploadToAzureDataLakeFailureTest {

    @Test
    void verifyErrorMessageAndCauseAreSet() {
        final Throwable cause = new Throwable();
        final AuditEntryUploadToAzureDataLakeFailure auditEntryUploadToAzureDataLakeFailure = new AuditEntryUploadToAzureDataLakeFailure("message", cause);
        assertThat(auditEntryUploadToAzureDataLakeFailure.getMessage(), is("message"));
        assertThat(auditEntryUploadToAzureDataLakeFailure.getCause(), is(cause));
    }
}