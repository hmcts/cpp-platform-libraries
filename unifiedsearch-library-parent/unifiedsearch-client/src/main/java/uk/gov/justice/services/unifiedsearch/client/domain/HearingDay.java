package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class HearingDay {

    private String courtCentreId;
    private String courtRoomId;
    private String sittingDay;
    private int listingSequence;
    private int listedDurationMinutes;
    private Boolean hasSharedResults;

    public Boolean getHasSharedResults() {
        return hasSharedResults;
    }

    public void setHasSharedResults(final Boolean hasSharedResults) {
        this.hasSharedResults = hasSharedResults;
    }

    public String getSittingDay() {
        return sittingDay;
    }

    public void setSittingDay(final String sittingDay) {
        this.sittingDay = sittingDay;
    }

    public int getListingSequence() {
        return listingSequence;
    }

    public void setListingSequence(final int listingSequence) {
        this.listingSequence = listingSequence;
    }

    public int getListedDurationMinutes() {
        return listedDurationMinutes;
    }

    public void setListedDurationMinutes(final int listedDurationMinutes) {
        this.listedDurationMinutes = listedDurationMinutes;
    }

    public String getCourtCentreId() {
        return courtCentreId;
    }

    public void setCourtCentreId(final String courtCentreId) {
        this.courtCentreId = courtCentreId;
    }

    public String getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(final String courtRoomId) {
        this.courtRoomId = courtRoomId;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HearingDay that = (HearingDay) o;
        return listingSequence == that.listingSequence &&
                listedDurationMinutes == that.listedDurationMinutes &&
                Objects.equals(courtCentreId, that.courtCentreId) &&
                Objects.equals(courtRoomId, that.courtRoomId) &&
                Objects.equals(sittingDay, that.sittingDay) &&
                Objects.equals(hasSharedResults, that.hasSharedResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courtCentreId, courtRoomId, sittingDay, listingSequence, listedDurationMinutes, hasSharedResults);
    }
}
