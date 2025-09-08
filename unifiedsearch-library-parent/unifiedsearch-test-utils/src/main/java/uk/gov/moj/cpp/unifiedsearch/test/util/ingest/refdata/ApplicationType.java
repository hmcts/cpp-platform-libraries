package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata;

public class ApplicationType {

    private final String applicationCode;
    private final String applicationType;

    public ApplicationType(final String applicationCode, final String applicationType) {
        this.applicationCode = applicationCode;
        this.applicationType = applicationType;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public String getApplicationType() {
        return applicationType;
    }
}
