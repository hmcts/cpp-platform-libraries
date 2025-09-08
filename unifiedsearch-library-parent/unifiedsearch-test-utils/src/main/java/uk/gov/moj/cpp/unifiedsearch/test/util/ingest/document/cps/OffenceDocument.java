package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

public class OffenceDocument {

    private String offenceId;
    private String offenceCode;
    private String custodyTimeLimit;
    private String type;
    private String description;
    private String startDate;
    private String endDate;

    public OffenceDocument(final String offenceId, final String offenceCode, final String custodyTimeLimit, final String type, final String description, final String startDate, final String endDate) {

        this.offenceId = offenceId;
        this.offenceCode = offenceCode;
        this.custodyTimeLimit = custodyTimeLimit;
        this.type = type;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getOffenceId() {
        return offenceId;
    }

    public void setOffenceId(final String offenceId) {
        this.offenceId = offenceId;
    }

    public String getOffenceCode() {
        return offenceCode;
    }

    public void setOffenceCode(final String offenceCode) {
        this.offenceCode = offenceCode;
    }

    public String getCustodyTimeLimit() {
        return custodyTimeLimit;
    }

    public void setCustodyTimeLimit(final String custodyTimeLimit) {
        this.custodyTimeLimit = custodyTimeLimit;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public String getOffenceEndDate() {
        return endDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    public static class Builder {

        private String offenceId;
        private String offenceCode;
        private String custodyTimeLimit;
        private String type;
        private String description;
        private String startDate;
        private String endDate;

        public Builder withOffenceId(final String offenceId) {
            this.offenceId = offenceId;
            return this;
        }

        public Builder withOffenceCode(final String offenceCode) {
            this.offenceCode = offenceCode;
            return this;
        }

        public Builder withCustodyTimeLimit(final String custodyTimeLimit) {
            this.custodyTimeLimit = custodyTimeLimit;
            return this;
        }

        public Builder withType(final String type) {
            this.type = type;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withStartDate(final String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(final String endDate) {
            this.endDate = endDate;
            return this;
        }

        public OffenceDocument build() {
            return new OffenceDocument(offenceId, offenceCode, custodyTimeLimit, type, description, startDate, endDate);
        }

    }
}