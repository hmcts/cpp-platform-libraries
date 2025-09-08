package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class LaaReferenceDocument {

    private String applicationReference;
    private String statusId;
    private String statusCode;
    private String statusDescription;

    public LaaReferenceDocument(final String applicationReference, final String statusId, final String statusCode, final String statusDescription) {
        this.applicationReference = applicationReference;
        this.statusId = statusId;
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
    }

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

    public static class Builder {

        private String applicationReference;
        private String statusId;
        private String statusCode;
        private String statusDescription;

        public Builder withApplicationReference(final String applicationReference) {
            this.applicationReference = applicationReference;
            return this;
        }

        public Builder withStatusId(final String statusId) {
            this.statusId = statusId;
            return this;
        }

        public Builder withStatusCode(final String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withStatusDescription(final String statusDescription) {
            this.statusDescription = statusDescription;
            return this;
        }

        public LaaReferenceDocument build() {
            return new LaaReferenceDocument(applicationReference, statusId, statusCode, statusDescription) ;
        }
    }
}
