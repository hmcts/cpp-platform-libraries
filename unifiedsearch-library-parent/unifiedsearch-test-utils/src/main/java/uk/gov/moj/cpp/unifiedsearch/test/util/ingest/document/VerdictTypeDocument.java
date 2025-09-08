package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class VerdictTypeDocument {
    private String description;
    private String category;
    private String categoryType;
    private int sequence;
    private String verdictTypeId;

    public VerdictTypeDocument(final String description, final String category, final String categoryType, final int sequence, final String verdictTypeId) {
        this.description = description;
        this.category = category;
        this.categoryType = categoryType;
        this.sequence = sequence;
        this.verdictTypeId = verdictTypeId;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public int getSequence() {
        return sequence;
    }

    public String getVerdictTypeId() {
        return verdictTypeId;
    }

    public static class Builder{
        private String description;
        private String category;
        private String categoryType;
        private int sequence;
        private String verdictTypeId;

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withCategory(final String category) {
            this.category = category;
            return this;
        }

        public Builder withCategoryType(final String categoryType) {
            this.categoryType = categoryType;
            return this;
        }

        public Builder withSequence(final int sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder withVerdictTypeId(final String verdictTypeId) {
            this.verdictTypeId = verdictTypeId;
            return this;
        }

        public VerdictTypeDocument build(){
            return new VerdictTypeDocument(description, category, categoryType, sequence, verdictTypeId);
        }
    }
}
