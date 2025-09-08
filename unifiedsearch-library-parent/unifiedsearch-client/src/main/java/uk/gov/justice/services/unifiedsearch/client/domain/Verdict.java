package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class Verdict {
    private String verdictDate;
    private String originatingHearingId;
    private VerdictType verdictType;

    public String getVerdictDate() {
        return verdictDate;
    }

    public void setVerdictDate(final String verdictDate) {
        this.verdictDate = verdictDate;
    }

    public String getOriginatingHearingId() {
        return originatingHearingId;
    }

    public void setOriginatingHearingId(final String originatingHearingId) {
        this.originatingHearingId = originatingHearingId;
    }

    public VerdictType getVerdictType() {
        return verdictType;
    }

    public void setVerdictType(final VerdictType verdictType) {
        this.verdictType = verdictType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Verdict verdict = (Verdict) o;
        return Objects.equals(verdictDate, verdict.verdictDate) && Objects.equals(originatingHearingId, verdict.originatingHearingId) && Objects.equals(verdictType, verdict.verdictType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdictDate, originatingHearingId, verdictType);
    }
}
