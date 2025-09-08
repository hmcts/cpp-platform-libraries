package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

public class AliasDocument {

    private String firstName;
    private String lastName;

    public AliasDocument() {
    }

    public AliasDocument(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
