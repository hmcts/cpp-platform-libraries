package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class VerdictType {
    private String description;
    private String category;
    private String categoryType;
    private int sequence;
    private String verdictTypeId;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final String categoryType) {
        this.categoryType = categoryType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(final int sequence) {
        this.sequence = sequence;
    }

    public String getVerdictTypeId() {
        return verdictTypeId;
    }

    public void setVerdictTypeId(final String verdictTypeId) {
        this.verdictTypeId = verdictTypeId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VerdictType that = (VerdictType) o;
        return Objects.equals(description, that.description) && Objects.equals(category, that.category) && Objects.equals(categoryType, that.categoryType) && Objects.equals(sequence, that.sequence) && Objects.equals(verdictTypeId, that.verdictTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, category, categoryType, sequence, verdictTypeId);
    }
}
