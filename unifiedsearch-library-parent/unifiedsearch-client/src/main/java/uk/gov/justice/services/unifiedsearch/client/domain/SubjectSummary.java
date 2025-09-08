package uk.gov.justice.services.unifiedsearch.client.domain;

public class SubjectSummary {
    private String middleName;
    private String lastName;
    private String subjectId;
    private String masterDefendantId;
    private String organisationName;
    private String firstName;
    private String dateOfBirth;
    private Address address;

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }


    public String getSubjectId() {
        return subjectId;
    }

    public void setMasterDefendantId(String masterDefendantId) {
        this.masterDefendantId = masterDefendantId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getMasterDefendantId() {
        return masterDefendantId;
    }

    @Override
    public String toString() {
        return "SubjectSummary{" +
                "subjectId='" + subjectId + '\'' +
                ", masterDefendantId='" + masterDefendantId + '\'' +
                ", organisationName='" + organisationName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", address=" + address +
                '}';
    }
}
