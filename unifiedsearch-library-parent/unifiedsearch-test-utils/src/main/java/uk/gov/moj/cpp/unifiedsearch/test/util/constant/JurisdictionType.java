package uk.gov.moj.cpp.unifiedsearch.test.util.constant;

public enum JurisdictionType {

    CROWN("Crown"),
    MAGISTRATES("Magistrates"),
    COURT_OF_APPEAL("Court Of Appeal");

    private String type;

    JurisdictionType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
