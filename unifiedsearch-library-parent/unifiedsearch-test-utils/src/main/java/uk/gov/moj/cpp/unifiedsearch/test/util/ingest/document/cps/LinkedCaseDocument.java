package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

public class LinkedCaseDocument {

    private String linkedCaseId;
    private Boolean manuallyLinked;

    public LinkedCaseDocument() {
    }

    public LinkedCaseDocument(final String linkedCaseId, final Boolean manuallyLinked) {
        this.linkedCaseId = linkedCaseId;
        this.manuallyLinked = manuallyLinked;
    }

    public String getLinkedCaseId() {
        return linkedCaseId;
    }

    public Boolean getManuallyLinked() {
        return manuallyLinked;
    }

    public static final class Builder {
        private String linkedCaseId;
        private Boolean manuallyLinked;

        public Builder withLinkedCaseId(final String linkedCaseId) {
            this.linkedCaseId = linkedCaseId;
            return this;
        }

        public Builder withManuallyLinked(final Boolean manuallyLinked) {
            this.manuallyLinked = manuallyLinked;
            return this;
        }

        public LinkedCaseDocument build() {
            return new LinkedCaseDocument(linkedCaseId, manuallyLinked);
        }
    }
}
