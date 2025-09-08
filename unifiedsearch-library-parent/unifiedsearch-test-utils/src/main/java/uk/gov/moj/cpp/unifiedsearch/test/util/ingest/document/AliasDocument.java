package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class AliasDocument {

    private String firstName;
    private String middleName;
    private String lastName;
    private String organisationName;

    public AliasDocument() {
    }

    public AliasDocument(final String organisationName) {
        this.organisationName = organisationName;
    }

    public AliasDocument(final String firstName, final String middleName, final String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }


}
