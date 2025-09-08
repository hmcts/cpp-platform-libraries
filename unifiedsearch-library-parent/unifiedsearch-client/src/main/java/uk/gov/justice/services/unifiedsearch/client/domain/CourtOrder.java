package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class CourtOrder {
    private String id;
    private String judicialResultTypeId;
    private String label;
    private String orderDate;
    private String startDate;
    private String endDate;
    private String orderingHearingId;
    private Boolean isSJPOrder;
    private Boolean canBeSubjectOfBreachProceedings;
    private Boolean canBeSubjectOfVariationProceedings;

    public String getId() {
        return id;
    }

    public String getJudicialResultTypeId() {
        return judicialResultTypeId;
    }

    public String getLabel() {
        return label;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getOrderingHearingId() {
        return orderingHearingId;
    }

    public Boolean getIsSJPOrder() {
        return isSJPOrder;
    }

    public Boolean getCanBeSubjectOfBreachProceedings() {
        return canBeSubjectOfBreachProceedings;
    }

    public Boolean getCanBeSubjectOfVariationProceedings() {
        return canBeSubjectOfVariationProceedings;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setJudicialResultTypeId(final String judicialResultTypeId) {
        this.judicialResultTypeId = judicialResultTypeId;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setOrderDate(final String orderDate) {
        this.orderDate = orderDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    public void setOrderingHearingId(final String orderingHearingId) {
        this.orderingHearingId = orderingHearingId;
    }

    public void setIsSJPOrder(final Boolean isSJPOrder) {
        this.isSJPOrder = isSJPOrder;
    }

    public void setCanBeSubjectOfBreachProceedings(final Boolean canBeSubjectOfBreachProceedings) {
        this.canBeSubjectOfBreachProceedings = canBeSubjectOfBreachProceedings;
    }

    public void setCanBeSubjectOfVariationProceedings(final Boolean canBeSubjectOfVariationProceedings) {
        this.canBeSubjectOfVariationProceedings = canBeSubjectOfVariationProceedings;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CourtOrder that = (CourtOrder) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(judicialResultTypeId, that.judicialResultTypeId) &&
                Objects.equals(label, that.label) &&
                Objects.equals(orderDate, that.orderDate) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(orderingHearingId, that.orderingHearingId) &&
                Objects.equals(isSJPOrder, that.isSJPOrder) &&
                Objects.equals(canBeSubjectOfBreachProceedings, that.canBeSubjectOfBreachProceedings) &&
                Objects.equals(canBeSubjectOfVariationProceedings, that.canBeSubjectOfVariationProceedings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, judicialResultTypeId, label, orderDate, startDate, endDate, orderingHearingId, isSJPOrder, canBeSubjectOfBreachProceedings, canBeSubjectOfVariationProceedings);
    }
}
