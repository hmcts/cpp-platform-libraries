package uk.gov.justice.services.unifiedsearch.client.domain.cps;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Party {

    private String partyId;
    private List<String> _party_type;
    private String firstName;
    private String lastName;
    private Set<Alias> aliases;
    private String dateOfBirth;
    private String asn;
    private String pncId;
    private String organisationName;
    private String oicShoulderNumber;
    private List<Offence> offences;

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(final String partyId) {
        this.partyId = partyId;
    }

    public List<String> get_party_type() {
        return _party_type;
    }

    public void set_party_type(final List<String> _party_type) {
        this._party_type = _party_type;
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

    public Set<Alias> getAliases() {
        return aliases;
    }

    public void setAliases(final Set<Alias> aliases) {
        this.aliases = aliases;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAsn() {
        return asn;
    }

    public void setAsn(final String asn) {
        this.asn = asn;
    }

    public String getPncId() {
        return pncId;
    }

    public void setPncId(final String pncId) {
        this.pncId = pncId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOicShoulderNumber() {
        return oicShoulderNumber;
    }

    public void setOicShoulderNumber(final String oicShoulderNumber) {
        this.oicShoulderNumber = oicShoulderNumber;
    }

    public List<Offence> getOffences() {
        return offences;
    }

    public void setOffences(final List<Offence> offences) {
        this.offences = offences;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Party party = (Party) o;
        return Objects.equals(partyId, party.partyId) &&
                Objects.equals(_party_type, party._party_type) &&
                Objects.equals(firstName, party.firstName) &&
                Objects.equals(lastName, party.lastName) &&
                Objects.equals(aliases, party.aliases) &&
                Objects.equals(dateOfBirth, party.dateOfBirth) &&
                Objects.equals(asn, party.asn) &&
                Objects.equals(pncId, party.pncId) &&
                Objects.equals(organisationName, party.organisationName) &&
                Objects.equals(oicShoulderNumber, party.oicShoulderNumber) &&
                Objects.equals(offences, party.offences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, _party_type, firstName, lastName, aliases, dateOfBirth, asn, pncId, organisationName, oicShoulderNumber, offences);
    }
}
