package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

import java.util.List;

public class OffenceDocument {

    private String offenceId;
    private String offenceCode;
    private String offenceTitle;
    private String offenceLegislation;
    private boolean proceedingsConcluded;
    private String arrestDate;
    private String dateOfInformation;
    private String endDate;
    private String startDate;
    private String chargeDate;
    private String modeOfTrial;
    private int orderIndex;
    private String wording;
    private LaaReferenceDocument laaReference;
    private List<CourtOrderDocument> courtOrders;
    private List<PleaDocument> pleas;
    private VerdictDocument verdict;

    public OffenceDocument(final String offenceId, final String offenceCode, final String offenceTitle, final String offenceLegislation, final boolean proceedingsConcluded,
                           final String arrestDate, final String dateOfInformation, final String endDate, final String startDate, final String chargeDate, final String modeOfTrial,
                           final int orderIndex, final String wording, final LaaReferenceDocument laaReference, final List<CourtOrderDocument> courtOrders, final List<PleaDocument> pleas,
                           final VerdictDocument verdict) {

        this.offenceId = offenceId;
        this.offenceCode = offenceCode;
        this.offenceTitle = offenceTitle;
        this.offenceLegislation = offenceLegislation;
        this.proceedingsConcluded = proceedingsConcluded;
        this.arrestDate = arrestDate;
        this.dateOfInformation = dateOfInformation;
        this.endDate = endDate;
        this.startDate = startDate;
        this.chargeDate = chargeDate;
        this.modeOfTrial = modeOfTrial;
        this.orderIndex = orderIndex;
        this.wording = wording;
        this.laaReference = laaReference;
        this.courtOrders = courtOrders;
        this.pleas = pleas;
        this.verdict = verdict;
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

    public String getOffenceTitle() {
        return offenceTitle;
    }

    public void setOffenceTitle(final String offenceTitle) {
        this.offenceTitle = offenceTitle;
    }

    public String getOffenceLegislation() {
        return offenceLegislation;
    }

    public void setOffenceLegislation(final String offenceLegislation) {
        this.offenceLegislation = offenceLegislation;
    }

    public boolean getProceedingsConcluded() {
        return proceedingsConcluded;
    }

    public void setProceedingsConcluded(final boolean proceedingsConcluded) {
        this.proceedingsConcluded = proceedingsConcluded;
    }

    public String getArrestDate() {
        return arrestDate;
    }

    public void setArrestDate(final String arrestDate) {
        this.arrestDate = arrestDate;
    }

    public String getDateOfInformation() {
        return dateOfInformation;
    }

    public void setDateOfInformation(final String dateOfInformation) {
        this.dateOfInformation = dateOfInformation;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public String getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(final String chargeDate) {
        this.chargeDate = chargeDate;
    }

    public String getModeOfTrial() {
        return modeOfTrial;
    }

    public void setModeOfTrial(final String modeOfTrial) {
        this.modeOfTrial = modeOfTrial;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(final int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getWording() {
        return wording;
    }

    public void setWording(final String wording) {
        this.wording = wording;
    }

    public LaaReferenceDocument getLaaReference() {
        return laaReference;
    }

    public void setLaaReference(final LaaReferenceDocument laaReference) {
        this.laaReference = laaReference;
    }

    public List<CourtOrderDocument> getCourtOrders() {
        return courtOrders;
    }

    public List<PleaDocument> getPleas() {
        return pleas;
    }

    public VerdictDocument getVerdict() {
        return verdict;
    }

    public static class Builder {

        private String offenceId;
        private String offenceCode;
        private String offenceTitle;
        private String offenceLegislation;
        private boolean proceedingsConcluded;
        private String arrestDate;
        private String dateOfInformation;
        private String endDate;
        private String startDate;
        private String chargeDate;
        private String modeOfTrial;
        private int orderIndex;
        private String wording;
        private LaaReferenceDocument laaReference;
        private List<CourtOrderDocument> courtOrders;
        private List<PleaDocument> pleas;
        private VerdictDocument verdict;

        public Builder withOffenceId(final String offenceId) {
            this.offenceId = offenceId;
            return this;
        }

        public Builder withOffenceCode(final String offenceCode) {
            this.offenceCode = offenceCode;
            return this;
        }

        public Builder withOffenceTitle(final String offenceTitle) {
            this.offenceTitle = offenceTitle;
            return this;
        }

        public Builder withOffenceLegislation(final String offenceLegislation) {
            this.offenceLegislation = offenceLegislation;
            return this;
        }

        public Builder withProceedingsConcluded(final boolean proceedingsConcluded) {
            this.proceedingsConcluded = proceedingsConcluded;
            return this;
        }

        public Builder withArrestDate(final String arrestDate) {
            this.arrestDate = arrestDate;
            return this;
        }

        public Builder withDateOfInformation(final String dateOfInformation) {
            this.dateOfInformation = dateOfInformation;
            return this;
        }

        public Builder withEndDate(final String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withStartDate(final String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withChargeDate(final String chargeDate) {
            this.chargeDate = chargeDate;
            return this;
        }

        public Builder withModeOfTrial(final String modeOfTrial) {
            this.modeOfTrial = modeOfTrial;
            return this;
        }

        public Builder withOrderIndex(final int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder withWording(final String wording) {
            this.wording = wording;
            return this;
        }

        public Builder withLaaReference(final LaaReferenceDocument laaReference) {
            this.laaReference = laaReference;
            return this;
        }

        public Builder withCourtOrders(final List<CourtOrderDocument> courtOrders) {
            this.courtOrders = courtOrders;
            return this;
        }

        public Builder withPleas(final List<PleaDocument> pleas) {
            this.pleas = pleas;
            return this;
        }

        public Builder withVerdict(final VerdictDocument verdict) {
            this.verdict = verdict;
            return this;
        }

        public OffenceDocument build() {
            return new OffenceDocument(offenceId, offenceCode, offenceTitle, offenceLegislation, proceedingsConcluded,
                    arrestDate, dateOfInformation, endDate, startDate, chargeDate, modeOfTrial,
                    orderIndex, wording, laaReference, courtOrders, pleas, verdict);
        }

    }
}
