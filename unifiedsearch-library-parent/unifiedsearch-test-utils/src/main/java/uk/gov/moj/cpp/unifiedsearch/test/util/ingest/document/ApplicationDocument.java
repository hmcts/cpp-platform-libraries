package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

@SuppressWarnings("squid:S107")
public class ApplicationDocument {

    private String applicationId;
    private String applicationReference;
    private String applicationType;
    private String applicationTypeCode;
    private String receivedDate;
    private String decisionDate;
    private String dueDate;
    private String applicationStatus;
    private String applicationExternalCreatorType;
    private SubjectDocument subjectSummary;

    private ApplicationDocument() {
    }

    public ApplicationDocument(String applicationId, String applicationReference, String applicationType, String receivedDate, String decisionDate, String dueDate, String applicationStatus, String applicationExternalCreatorType, SubjectDocument subjectSummary, String applicationTypeCode) {
        this.applicationId = applicationId;
        this.applicationReference = applicationReference;
        this.applicationType = applicationType;
        this.receivedDate = receivedDate;
        this.decisionDate = decisionDate;
        this.dueDate = dueDate;
        this.applicationStatus = applicationStatus;
        this.applicationExternalCreatorType = applicationExternalCreatorType;
        this.subjectSummary = subjectSummary;
        this.applicationTypeCode = applicationTypeCode;

    }

    public SubjectDocument getSubjectSummary() {
        return subjectSummary;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public String getApplicationTypeCode() {
        return applicationTypeCode;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public String getDecisionDate() {
        return decisionDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public String getApplicationExternalCreatorType() {
        return applicationExternalCreatorType;
    }

    public static class Builder {

        private String applicationId;
        private String applicationReference;
        private String applicationType;
        private String applicationTypeCode;
        private String receivedDate;
        private String decisionDate;
        private String dueDate;
        private String applicationStatus;
        private String applicationExternalCreatorType;
        private SubjectDocument subjectSummary;

        public Builder withSubjectSummary(SubjectDocument subjectSummary) {
            this.subjectSummary = subjectSummary;
            return this;
        }

        public Builder withApplicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder withApplicationReference(String applicationReference) {
            this.applicationReference = applicationReference;
            return this;
        }

        public Builder withApplicationType(String applicationType) {
            this.applicationType = applicationType;
            return this;
        }

        public Builder withApplicationTypeCode(String applicationTypeCode) {
            this.applicationTypeCode = applicationTypeCode;
            return this;
        }

        public Builder withReceivedDate(String receivedDate) {
            this.receivedDate = receivedDate;
            return this;
        }

        public Builder withDecisionDate(String decisionDate) {
            this.decisionDate = decisionDate;
            return this;
        }

        public Builder withDueDate(String dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder withApplicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public Builder withApplicationExternalCreatorType(String applicationExternalCreatorType) {
            this.applicationExternalCreatorType = applicationExternalCreatorType;
            return this;
        }

        public ApplicationDocument build() {
            return new ApplicationDocument(applicationId, applicationReference, applicationType, receivedDate, decisionDate, dueDate, applicationStatus, applicationExternalCreatorType, subjectSummary, applicationTypeCode);
        }
    }
}
