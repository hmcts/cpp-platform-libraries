package uk.gov.justice.services.unifiedsearch.client.domain.cps;


import java.util.Objects;

public class Hearing {

    private String hearingId;
    private String hearingDateTime;
    private String courtHouse;
    private String courtRoom;
    private String hearingType;
    private String jurisdiction;

    public String getHearingId() {
        return hearingId;
    }

    public void setHearingId(final String hearingId) {
        this.hearingId = hearingId;
    }

    public String getHearingDateTime() {
        return hearingDateTime;
    }

    public void setHearingDateTime(final String hearingDateTime) {
        this.hearingDateTime = hearingDateTime;
    }

    public String getCourtHouse() {
        return courtHouse;
    }

    public void setCourtHouse(final String courtHouse) {
        this.courtHouse = courtHouse;
    }

    public String getCourtRoom() {
        return courtRoom;
    }

    public void setCourtRoom(final String courtRoom) {
        this.courtRoom = courtRoom;
    }

    public String getHearingType() {
        return hearingType;
    }

    public void setHearingType(final String hearingType) {
        this.hearingType = hearingType;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(final String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Hearing hearing = (Hearing) o;
        return Objects.equals(hearingId, hearing.hearingId) &&
                Objects.equals(hearingDateTime, hearing.hearingDateTime) &&
                Objects.equals(courtHouse, hearing.courtHouse) &&
                Objects.equals(courtRoom, hearing.courtRoom) &&
                Objects.equals(hearingType, hearing.hearingType) &&
                Objects.equals(jurisdiction, hearing.jurisdiction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hearingId, hearingDateTime, courtHouse, courtRoom, hearingType, jurisdiction);
    }
}
