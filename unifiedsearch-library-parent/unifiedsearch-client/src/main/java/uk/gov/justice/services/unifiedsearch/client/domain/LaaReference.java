package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class LaaReference {

    private String applicationReference;
    private String statusId;
    private String statusCode;
    private String statusDescription;

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(final String applicationReference) {
        this.applicationReference = applicationReference;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(final String statusId) {
        this.statusId = statusId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(final String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LaaReference that = (LaaReference) o;
        return Objects.equals(applicationReference, that.applicationReference) &&
                Objects.equals(statusId, that.statusId) &&
                Objects.equals(statusCode, that.statusCode) &&
                Objects.equals(statusDescription, that.statusDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationReference, statusId, statusCode, statusDescription);
    }
}
