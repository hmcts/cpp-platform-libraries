package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class VerdictDocument {
    private String verdictDate;
    private String originatingHearingId;
    private VerdictTypeDocument verdictType;

    public VerdictDocument(final String verdictDate, final String originatingHearingId, final VerdictTypeDocument verdictType) {
        this.verdictDate = verdictDate;
        this.originatingHearingId = originatingHearingId;
        this.verdictType = verdictType;
    }

    public String getVerdictDate() {
        return verdictDate;
    }

    public String getOriginatingHearingId() {
        return originatingHearingId;
    }

    public VerdictTypeDocument getVerdictType() {
        return verdictType;
    }

    public static class Builder {
        private String verdictDate;
        private String originatingHearingId;
        private VerdictTypeDocument verdictType;

        public Builder withVerdictDate(final String verdictDate) {
            this.verdictDate = verdictDate;
            return this;
        }

        public Builder withOriginatingHearingId(final String originatingHearingId) {
            this.originatingHearingId = originatingHearingId;
            return this;
        }

        public Builder withVerdictType(final VerdictTypeDocument verdictType) {
            this.verdictType = verdictType;
            return this;
        }

        public VerdictDocument build(){
            return new VerdictDocument(verdictDate, originatingHearingId, verdictType);
        }
    }
}
