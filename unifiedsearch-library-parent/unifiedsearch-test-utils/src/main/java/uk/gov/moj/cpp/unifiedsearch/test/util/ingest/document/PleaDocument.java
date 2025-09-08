package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class PleaDocument {
    private String originatingHearingId;
    private String pleaValue;
    private String pleaDate;

    public PleaDocument(final String originatingHearingId, final String pleaValue, final String pleaDate) {
        this.originatingHearingId = originatingHearingId;
        this.pleaValue = pleaValue;
        this.pleaDate = pleaDate;
    }

    public String getOriginatingHearingId() {
        return originatingHearingId;
    }

    public String getPleaValue() {
        return pleaValue;
    }

    public String getPleaDate() {
        return pleaDate;
    }

    public static class Builder {
        private String originatingHearingId;
        private String pleaValue;
        private String pleaDate;

        public PleaDocument.Builder withOriginatingHearingId(final String originatingHearingId) {
            this.originatingHearingId = originatingHearingId;
            return this;
        }

        public PleaDocument.Builder withPleaValue(final String pleaValue) {
            this.pleaValue = pleaValue;
            return this;
        }

        public PleaDocument.Builder withPleaDate(final String pleaDate) {
            this.pleaDate = pleaDate;
            return this;
        }

        public PleaDocument build() {
            return new PleaDocument(originatingHearingId, pleaValue, pleaDate);
        }
    }
}
