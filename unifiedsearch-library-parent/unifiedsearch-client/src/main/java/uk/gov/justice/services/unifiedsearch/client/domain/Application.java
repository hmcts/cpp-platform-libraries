package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class Application {

    private String applicationId;
    private String applicationReference;
    private String applicationType;
    private String receivedDate;
    private String decisionDate;
    private String dueDate;
    private String applicationStatus;
    private String applicationExternalCreatorType;
    private SubjectSummary subjectSummary;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(final String applicationReference) {
        this.applicationReference = applicationReference;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(final String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(final String decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public Application setDueDate(final String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(final String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationExternalCreatorType() {
        return applicationExternalCreatorType;
    }

    public void setApplicationExternalCreatorType(final String applicationExternalCreatorType) {
        this.applicationExternalCreatorType = applicationExternalCreatorType;
    }

    public SubjectSummary getSubjectSummary() {
        return subjectSummary;
    }

    public void setSubjectSummary(SubjectSummary subjectSummary) {
        this.subjectSummary = subjectSummary;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Application that = (Application) o;
        return Objects.equals(applicationId, that.applicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId);
    }
}
