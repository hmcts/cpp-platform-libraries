package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class RepresentationOrder {
    private String applicationReference;
    private String effectiveFromDate;
    private String effectiveToDate;
    private String laaContractNumber;

    public String getApplicationReference() {
        return applicationReference;
    }

    public RepresentationOrder setApplicationReference(final String applicationReference) {
        this.applicationReference = applicationReference;
        return this;
    }

    public String getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public RepresentationOrder setEffectiveFromDate(final String effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
        return this;
    }

    public String getEffectiveToDate() {
        return effectiveToDate;
    }

    public RepresentationOrder setEffectiveToDate(final String effectiveToDate) {
        this.effectiveToDate = effectiveToDate;
        return this;
    }

    public String getLaaContractNumber() {
        return laaContractNumber;
    }

    public RepresentationOrder setLaaContractNumber(final String laaContractNumber) {
        this.laaContractNumber = laaContractNumber;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RepresentationOrder that = (RepresentationOrder) o;
        return Objects.equals(applicationReference, that.applicationReference) &&
                Objects.equals(effectiveFromDate, that.effectiveFromDate) &&
                Objects.equals(effectiveToDate, that.effectiveToDate) &&
                Objects.equals(laaContractNumber, that.laaContractNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationReference, effectiveFromDate, effectiveToDate, laaContractNumber);
    }
}
