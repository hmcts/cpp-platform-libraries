package uk.gov.justice.services.unifiedsearch.client.domain.cps;

import java.util.Objects;

public class Offence {

    private String offenceId;
    private String offenceCode;
    private String custodyTimeLimit;
    private String type;
    private String description;
    private String startDate;
    private String endDate;

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

    public String getCustodyTimeLimit() {
        return custodyTimeLimit;
    }

    public void setCustodyTimeLimit(final String offenceTitle) {
        this.custodyTimeLimit = custodyTimeLimit;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Offence offence = (Offence) o;
        return Objects.equals(offenceId, offence.offenceId) &&
                Objects.equals(offenceCode, offence.offenceCode) &&
                Objects.equals(custodyTimeLimit, offence.custodyTimeLimit) &&
                Objects.equals(type, offence.type) &&
                Objects.equals(description, offence.description) &&
                Objects.equals(startDate, offence.startDate) &&
                Objects.equals(endDate, offence.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offenceId, offenceCode, custodyTimeLimit, type, description, startDate, endDate);
    }
}
