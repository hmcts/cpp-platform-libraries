package uk.gov.justice.services.unifiedsearch.client.domain.cps;

import java.util.Objects;

public class LinkedCase {

    private String linkedCaseId;
    private Boolean manuallyLinked;

    public String getLinkedCaseId() {
        return linkedCaseId;
    }

    public void setLinkedCaseId(final String linkedCaseId) {
        this.linkedCaseId = linkedCaseId;
    }

    public Boolean getManuallyLinked() {
        return manuallyLinked;
    }

    public void setManuallyLinked(final Boolean manuallyLinked) {
        this.manuallyLinked = manuallyLinked;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LinkedCase linkedCase = (LinkedCase) o;
        return Objects.equals(linkedCaseId, linkedCase.linkedCaseId) &&
                Objects.equals(manuallyLinked, linkedCase.manuallyLinked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkedCaseId, manuallyLinked);
    }
}
