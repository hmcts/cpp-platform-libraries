package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

public class HearingDocument {

    private String hearingId;
    private String hearingDateTime;
    private String courtHouse;
    private String courtRoom;
    private String hearingType;
    private String jurisdiction;

    private HearingDocument() {
    }


    public HearingDocument(final String hearingId, final String hearingDateTime, final String courtHouse, final String courtRoom, final String hearingType, final String jurisdiction) {
        this.hearingId = hearingId;
        this.hearingDateTime = hearingDateTime;
        this.courtHouse = courtHouse;
        this.courtRoom = courtRoom;
        this.hearingType = hearingType;
        this.jurisdiction = jurisdiction;
    }

    public String getHearingId() {
        return hearingId;
    }

    public String getHearingDateTime() {
        return hearingDateTime;
    }

    public String getCourtHouse() {
        return courtHouse;
    }

    public String getCourtRoom() {
        return courtRoom;
    }

    public String getHearingType() {
        return hearingType;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public static final class Builder {

        private String hearingId;
        private String hearingDateTime;
        private String courtHouse;
        private String courtRoom;
        private String hearingType;
        private String jurisdiction;


        public Builder withHearingId(final String hearingId) {
            this.hearingId = hearingId;
            return this;
        }

        public Builder withHearingDateTime(final String hearingDateTime) {
            this.hearingDateTime = hearingDateTime;
            return this;
        }

        public Builder withCourtHouse(final String courtHouse) {
            this.courtHouse = courtHouse;
            return this;
        }

        public Builder withCourtRoom(final String courtRoom) {
            this.courtRoom = courtRoom;
            return this;
        }

        public Builder withHearingType(final String hearingType) {
            this.hearingType = hearingType;
            return this;
        }

        public Builder withJurisdiction(final String jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public HearingDocument build() {
            return new HearingDocument(hearingId, hearingDateTime, courtHouse, courtRoom, hearingType, jurisdiction);
        }
    }
}
