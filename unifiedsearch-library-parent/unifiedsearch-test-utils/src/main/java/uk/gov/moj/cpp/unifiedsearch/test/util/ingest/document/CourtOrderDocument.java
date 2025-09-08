package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class CourtOrderDocument {
    private String id;
    private String judicialResultTypeId;
    private String label;
    private String orderDate;
    private String startDate;
    private String endDate;
    private String orderingHearingId;
    private boolean isSJPOrder;
    private boolean canBeSubjectOfBreachProceedings;
    private boolean canBeSubjectOfVariationProceedings;

    public CourtOrderDocument(final String id, final String judicialResultTypeId, final String label, final String orderDate, final String startDate, final String endDate, final String orderingHearingId, final boolean isSJPOrder, final boolean canBeSubjectOfBreachProceedings, final boolean canBeSubjectOfVariationProceedings) {
        this.id = id;
        this.judicialResultTypeId = judicialResultTypeId;
        this.label = label;
        this.orderDate = orderDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderingHearingId = orderingHearingId;
        this.isSJPOrder = isSJPOrder;
        this.canBeSubjectOfBreachProceedings = canBeSubjectOfBreachProceedings;
        this.canBeSubjectOfVariationProceedings = canBeSubjectOfVariationProceedings;
    }

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

    public boolean getIsSJPOrder() {
        return isSJPOrder;
    }

    public boolean getCanBeSubjectOfBreachProceedings() {
        return canBeSubjectOfBreachProceedings;
    }

    public boolean getCanBeSubjectOfVariationProceedings() {
        return canBeSubjectOfVariationProceedings;
    }

    public static class Builder {
        private String id;
        private String judicialResultTypeId;
        private String label;
        private String orderDate;
        private String startDate;
        private String endDate;
        private String orderingHearingId;
        private boolean isSJPOrder;
        private boolean canBeSubjectOfBreachProceedings;
        private boolean canBeSubjectOfVariationProceedings;

        public CourtOrderDocument.Builder withId(final String id) {
            this.id = id;
            return this;
        }

        public CourtOrderDocument.Builder withJudicialResultTypeId(final String judicialResultTypeId) {
            this.judicialResultTypeId = judicialResultTypeId;
            return this;
        }

        public CourtOrderDocument.Builder withLabel(final String label) {
            this.label = label;
            return this;
        }

        public CourtOrderDocument.Builder withOrderDate(final String orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public CourtOrderDocument.Builder withStartDate(final String startDate) {
            this.startDate = startDate;
            return this;
        }

        public CourtOrderDocument.Builder withEndDate(final String endDate) {
            this.endDate = endDate;
            return this;
        }

        public CourtOrderDocument.Builder withOrderingHearingId(final String orderingHearingId) {
            this.orderingHearingId = orderingHearingId;
            return this;
        }

        public CourtOrderDocument.Builder withIsSJPOrder(final boolean isSJPOrder) {
            this.isSJPOrder = isSJPOrder;
            return this;
        }

        public CourtOrderDocument.Builder withCanBeSubjectOfBreachProceedings(final boolean canBeSubjectOfBreachProceedings) {
            this.canBeSubjectOfBreachProceedings = canBeSubjectOfBreachProceedings;
            return this;
        }

        public CourtOrderDocument.Builder withCanBeSubjectOfVariationProceedings(final boolean canBeSubjectOfVariationProceedings) {
            this.canBeSubjectOfVariationProceedings = canBeSubjectOfVariationProceedings;
            return this;
        }


        public CourtOrderDocument build() {
            return new CourtOrderDocument(id, judicialResultTypeId, label, orderDate, startDate, endDate, orderingHearingId, isSJPOrder, canBeSubjectOfBreachProceedings, canBeSubjectOfVariationProceedings);
        }
    }

}
