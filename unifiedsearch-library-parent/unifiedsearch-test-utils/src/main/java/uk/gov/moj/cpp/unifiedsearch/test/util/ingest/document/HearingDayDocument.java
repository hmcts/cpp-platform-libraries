package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class HearingDayDocument {

    private String sittingDay;
    private int listingSequence;
    private int listedDurationMinutes;
    private String courtCentreId;
    private String courtRoomId;
    private Boolean hasSharedResults;


    public HearingDayDocument(final String sittingDay, final int listingSequence, final int listedDurationMinutes, final String courtCentreId
            , final String courtRoomId, final Boolean hasSharedResults) {
        this.sittingDay = sittingDay;
        this.listingSequence = listingSequence;
        this.listedDurationMinutes = listedDurationMinutes;
        this.courtCentreId = courtCentreId;
        this.courtRoomId = courtRoomId;
        this.hasSharedResults = hasSharedResults;
    }

    public String getSittingDay() {
        return sittingDay;
    }

    public int getListingSequence() {
        return listingSequence;
    }

    public int getListedDurationMinutes() {
        return listedDurationMinutes;
    }

    public String getCourtCentreId() {
        return courtCentreId;
    }

    public String getCourtRoomId() {
        return courtRoomId;
    }

    public Boolean getHasSharedResults() {
        return hasSharedResults;
    }

    public static class Builder {
        private String sittingDay;
        private int listingSequence;
        private int listedDurationMinutes;
        private String courtCentreId;
        private String courtRoomId;
        private Boolean hasSharedResults;

        public Builder withSittingDay(final String sittingDay) {
            this.sittingDay = sittingDay;
            return this;
        }

        public Builder withListingSequence(final int listingSequence) {
            this.listingSequence = listingSequence;
            return this;
        }

        public Builder withListedDurationMinutes(final int listedDurationMinutes) {
            this.listedDurationMinutes = listedDurationMinutes;
            return this;
        }

        public Builder withCourtCentreId(final String courtCentreId) {
            this.courtCentreId = courtCentreId;
            return this;
        }

        public Builder withCourtRoomId(final String courtRoomId) {
            this.courtRoomId = courtRoomId;
            return this;
        }

        public Builder withHasSharedResults(final Boolean hasSharedResults) {
            this.hasSharedResults = hasSharedResults;
            return this;
        }

        public HearingDayDocument build() {
            return new HearingDayDocument(sittingDay, listingSequence, listedDurationMinutes, courtCentreId, courtRoomId, hasSharedResults);
        }
    }

}
