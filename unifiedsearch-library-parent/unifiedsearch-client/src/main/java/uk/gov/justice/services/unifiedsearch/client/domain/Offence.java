package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.List;
import java.util.Objects;

public class Offence {

    private String offenceId;
    private String offenceCode;
    private String offenceTitle;
    private String offenceLegislation;
    private Boolean proceedingsConcluded;
    private String arrestDate;
    private String dateOfInformation;
    private String endDate;
    private String startDate;
    private String chargeDate;
    private String modeOfTrial;
    private Integer orderIndex;
    private String wording;
    private LaaReference laaReference;
    private List<CourtOrder> courtOrders;
    private List<Plea> pleas;
    private Verdict verdict;

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

    public Boolean getProceedingsConcluded() {
        return proceedingsConcluded;
    }

    public void setProceedingsConcluded(final Boolean proceedingsConcluded) {
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(final Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getWording() {
        return wording;
    }

    public void setWording(final String wording) {
        this.wording = wording;
    }

    public LaaReference getLaaReference() {
        return laaReference;
    }

    public void setLaaReference(final LaaReference laaReference) {
        this.laaReference = laaReference;
    }

    public List<CourtOrder> getCourtOrders() {
        return courtOrders;
    }

    public void setCourtOrders(final List<CourtOrder> courtOrders) {
        this.courtOrders = courtOrders;
    }

    public List<Plea> getPleas() {
        return pleas;
    }

    public void setPleas(final List<Plea> pleas) {
        this.pleas = pleas;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(final Verdict verdict) {
        this.verdict = verdict;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Offence offence = (Offence) o;
        return Objects.equals(offenceId, offence.offenceId) &&
                Objects.equals(offenceCode, offence.offenceCode) &&
                Objects.equals(offenceTitle, offence.offenceTitle) &&
                Objects.equals(offenceLegislation, offence.offenceLegislation) &&
                Objects.equals(proceedingsConcluded, offence.proceedingsConcluded) &&
                Objects.equals(arrestDate, offence.arrestDate) &&
                Objects.equals(dateOfInformation, offence.dateOfInformation) &&
                Objects.equals(endDate, offence.endDate) &&
                Objects.equals(startDate, offence.startDate) &&
                Objects.equals(chargeDate, offence.chargeDate) &&
                Objects.equals(modeOfTrial, offence.modeOfTrial) &&
                Objects.equals(orderIndex, offence.orderIndex) &&
                Objects.equals(wording, offence.wording) &&
                Objects.equals(laaReference, offence.laaReference) &&
                Objects.equals(courtOrders, offence.courtOrders) &&
                Objects.equals(pleas, offence.pleas) &&
                Objects.equals(verdict, offence.verdict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offenceId, offenceCode, offenceTitle, offenceLegislation, proceedingsConcluded, arrestDate, dateOfInformation, endDate, startDate, chargeDate, modeOfTrial, orderIndex, wording, laaReference, courtOrders, pleas);
    }
}
