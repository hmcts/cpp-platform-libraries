package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class Plea {

    private String originatingHearingId;
    private String pleaValue;
    private String pleaDate;

    public String getOriginatingHearingId() {
        return originatingHearingId;
    }

    public void setOriginatingHearingId(final String originatingHearingId) {
        this.originatingHearingId = originatingHearingId;
    }

    public String getPleaValue() {
        return pleaValue;
    }

    public void setPleaValue(final String pleaValue) {
        this.pleaValue = pleaValue;
    }

    public String getPleaDate() {
        return pleaDate;
    }

    public void setPleaDate(final String pleaDate) {
        this.pleaDate = pleaDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Plea plea = (Plea) o;
        return Objects.equals(originatingHearingId, plea.originatingHearingId) &&
                Objects.equals(pleaValue, plea.pleaValue) &&
                Objects.equals(pleaDate, plea.pleaDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originatingHearingId, pleaValue, pleaDate);
    }
}
