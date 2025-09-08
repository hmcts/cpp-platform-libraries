package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Party {
    private String partyId;
    private String masterPartyId;
    private String croNumber;
    private String courtProceedingsInitiated;
    private String firstName;
    private String middleName;
    private String lastName;
    private String title;
    private String dateOfBirth;
    private String gender;
    private String addressLines;
    private String postCode;
    private String pncId;
    private String arrestSummonsNumber;
    private String _party_type;
    private Set<Alias> aliases;
    private String organisationName;
    private String nationalInsuranceNumber;
    private Boolean proceedingsConcluded;
    private RepresentationOrder representationOrder;
    private Address defendantAddress;
    private List<Offence> offences;

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    public String get_party_type() {
        return _party_type;
    }

    public void set_party_type(final String _party_type) {
        this._party_type = _party_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
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

    public void setPartyId(final String partyId) {
        this.partyId = partyId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(final String addressLines) {
        this.addressLines = addressLines;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(final String postCode) {
        this.postCode = postCode;
    }

    public String getPncId() {
        return pncId;
    }

    public void setPncId(final String pncId) {
        this.pncId = pncId;
    }

    public Set<Alias> getAliases() {
        return aliases;
    }

    public void setAliases(final Set<Alias> aliases) {
        this.aliases = aliases;
    }

    public String getArrestSummonsNumber() {
        return arrestSummonsNumber;
    }

    public void setArrestSummonsNumber(final String arrestSummonsNumber) {
        this.arrestSummonsNumber = arrestSummonsNumber;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getNationalInsuranceNumber() {
        return nationalInsuranceNumber;
    }

    public void setNationalInsuranceNumber(final String nationalInsuranceNumber) {
        this.nationalInsuranceNumber = nationalInsuranceNumber;
    }

    public Boolean getProceedingsConcluded() {
        return proceedingsConcluded;
    }

    public void setProceedingsConcluded(final Boolean proceedingsConcluded) {
        this.proceedingsConcluded = proceedingsConcluded;
    }

    public RepresentationOrder getRepresentationOrder() {
        return representationOrder;
    }

    public void setRepresentationOrder(final RepresentationOrder representationOrder) {
        this.representationOrder = representationOrder;
    }


    public List<Offence> getOffences() {
        return offences;
    }

    public void setOffences(final List<Offence> offences) {
        this.offences = offences;
    }
    public String getMasterPartyId() {
        return masterPartyId;
    }

    public void setMasterPartyId(String masterPartyId) {
        this.masterPartyId = masterPartyId;
    }

    public String getCroNumber() {
        return croNumber;
    }

    public void setCroNumber(String croNumber) {
        this.croNumber = croNumber;
    }

    public String getCourtProceedingsInitiated() {
        return courtProceedingsInitiated;
    }

    public void setCourtProceedingsInitiated(String courtProceedingsInitiated) {
        this.courtProceedingsInitiated = courtProceedingsInitiated;
    }

    public Address getDefendantAddress() {
        return defendantAddress;
    }

    public void setDefendantAddress(Address defendantAddress) {
        this.defendantAddress = defendantAddress;
    }

    @Override
    public String toString() {
        return "Party{" +
                "partyId='" + partyId + '\'' +
                ", masterPartyId='" + masterPartyId + '\'' +
                ", croNumber='" + croNumber + '\'' +
                ", courtProceedingsInitiated='" + courtProceedingsInitiated + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", addressLines='" + addressLines + '\'' +
                ", postCode='" + postCode + '\'' +
                ", pncId='" + pncId + '\'' +
                ", arrestSummonsNumber='" + arrestSummonsNumber + '\'' +
                ", _party_type='" + _party_type + '\'' +
                ", aliases=" + aliases +
                ", organisationName='" + organisationName + '\'' +
                ", nationalInsuranceNumber='" + nationalInsuranceNumber + '\'' +
                ", proceedingsConcluded=" + proceedingsConcluded +
                ", representationOrder=" + representationOrder +
                ", defendantAddress=" + defendantAddress +
                ", offences=" + offences +
                '}';
    }

    public static Party from(final Party source) {

        final Party party = new Party();
        party.setPartyId(source.getPartyId());
        party.setCroNumber(source.getCroNumber());
        party.setCourtProceedingsInitiated(source.getCourtProceedingsInitiated());
        party.setMasterPartyId(source.getMasterPartyId());
        party.setFirstName(source.getFirstName());
        party.setMiddleName(source.getMiddleName());
        party.setLastName(source.getLastName());
        party.setTitle(source.getTitle());
        party.setDateOfBirth(source.getDateOfBirth());
        party.setGender(source.getGender());
        party.setAddressLines(source.getAddressLines());
        party.setDefendantAddress(source.getDefendantAddress());
        party.setPostCode(source.getPostCode());
        party.setPncId(source.getPncId());
        party.setArrestSummonsNumber(source.getArrestSummonsNumber());
        party.set_party_type(source.get_party_type());
        party.setAliases(source.getAliases());
        party.setOrganisationName(source.getOrganisationName());
        party.setNationalInsuranceNumber(source.getNationalInsuranceNumber());
        party.setProceedingsConcluded(source.getProceedingsConcluded());
        party.setRepresentationOrder(source.getRepresentationOrder());
        party.setOffences(source.getOffences());
        return party;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Party)) return false;
        Party party = (Party) o;
        return Objects.equals(getPartyId(), party.getPartyId()) &&
                Objects.equals(getMasterPartyId(), party.getMasterPartyId()) &&
                Objects.equals(getCroNumber(), party.getCroNumber()) &&
                Objects.equals(getCourtProceedingsInitiated(), party.getCourtProceedingsInitiated()) &&
                Objects.equals(getFirstName(), party.getFirstName()) &&
                Objects.equals(getMiddleName(), party.getMiddleName()) &&
                Objects.equals(getLastName(), party.getLastName()) &&
                Objects.equals(getTitle(), party.getTitle()) &&
                Objects.equals(getDateOfBirth(), party.getDateOfBirth()) &&
                Objects.equals(getGender(), party.getGender()) &&
                Objects.equals(getAddressLines(), party.getAddressLines()) &&
                Objects.equals(getPostCode(), party.getPostCode()) &&
                Objects.equals(getPncId(), party.getPncId()) &&
                Objects.equals(getArrestSummonsNumber(), party.getArrestSummonsNumber()) &&
                Objects.equals(get_party_type(), party.get_party_type()) &&
                Objects.equals(getAliases(), party.getAliases()) &&
                Objects.equals(getOrganisationName(), party.getOrganisationName()) &&
                Objects.equals(getNationalInsuranceNumber(), party.getNationalInsuranceNumber()) &&
                Objects.equals(getProceedingsConcluded(), party.getProceedingsConcluded()) &&
                Objects.equals(getRepresentationOrder(), party.getRepresentationOrder()) &&
                Objects.equals(getDefendantAddress(), party.getDefendantAddress()) &&
                Objects.equals(getOffences(), party.getOffences());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPartyId(), getMasterPartyId(), getCroNumber(), getCourtProceedingsInitiated(), getFirstName(), getMiddleName(), getLastName(), getTitle(), getDateOfBirth(), getGender(), getAddressLines(), getPostCode(), getPncId(), getArrestSummonsNumber(), get_party_type(), getAliases(), getOrganisationName(), getNationalInsuranceNumber(), getProceedingsConcluded(), getRepresentationOrder(), getDefendantAddress(), getOffences());
    }
}
