package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata;

import java.util.UUID;

public class Court {

    private final UUID courtId;
    private final String courtCentreName;

    public Court(final UUID courtId, final String courtCentreName) {
        this.courtId = courtId;
        this.courtCentreName = courtCentreName;
    }

    public UUID getCourtId() {
        return courtId;
    }

    public String getCourtCentreName() {
        return courtCentreName;
    }
}
