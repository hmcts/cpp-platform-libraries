package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class RepresentationOrderDocument {

    private String applicationReference;
    private String effectiveFromDate;
    private String effectiveToDate;
    private String laaContractNumber;

    public RepresentationOrderDocument(final String applicationReference, final String effectiveFromDate, final String effectiveToDate, final String laaContractNumber) {
        this.applicationReference = applicationReference;
        this.effectiveFromDate = effectiveFromDate;
        this.effectiveToDate = effectiveToDate;
        this.laaContractNumber = laaContractNumber;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public RepresentationOrderDocument setApplicationReference(final String applicationReference) {
        this.applicationReference = applicationReference;
        return this;
    }

    public String getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public RepresentationOrderDocument setEffectiveFromDate(final String effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
        return this;
    }

    public String getEffectiveToDate() {
        return effectiveToDate;
    }

    public RepresentationOrderDocument setEffectiveToDate(final String effectiveToDate) {
        this.effectiveToDate = effectiveToDate;
        return this;
    }

    public String getLaaContractNumber() {
        return laaContractNumber;
    }

    public RepresentationOrderDocument setLaaContractNumber(final String laaContractNumber) {
        this.laaContractNumber = laaContractNumber;
        return this;
    }

    public static class Builder {

        private String applicationReference;
        private String effectiveFromDate;
        private String effectiveToDate;
        private String laaContractNumber;

        public Builder withApplicationReference(final String applicationReference) {
            this.applicationReference = applicationReference;
            return this;
        }

        public Builder withEffectiveFromDate(final String effectiveFromDate) {
            this.effectiveFromDate = effectiveFromDate;
            return this;
        }

        public Builder withEffectiveToDate(final String effectiveToDate) {
            this.effectiveToDate = effectiveToDate;
            return this;
        }

        public Builder withLaaContractNumber(final String laaContractNumber) {
            this.laaContractNumber = laaContractNumber;
            return this;
        }

        public RepresentationOrderDocument build() {
            return new RepresentationOrderDocument(applicationReference, effectiveFromDate, effectiveToDate, laaContractNumber);
        }
    }
}
