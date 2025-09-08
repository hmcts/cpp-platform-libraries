package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

import java.util.List;

public class PartyDocument {

    private String partyId;
    private List<String> _party_type;
    private String firstName;
    private String lastName;
    private List<AliasDocument> aliases;
    private String dateOfBirth;
    private String asn;
    private String pncId;
    private String organisationName;
    private String oicShoulderNumber;
    private List<OffenceDocument> offences;

    private PartyDocument() {
    }

    public PartyDocument(final String partyId, final List<String> _party_type, final String firstName, final String lastName, final List<AliasDocument> aliases,
                         final String dateOfBirth, final String asn, final String pncId,
                         final String organisationName, final String oicShoulderNumber,
                         final List<OffenceDocument> offences) {
        this.partyId = partyId;
        this._party_type = _party_type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.aliases = aliases;
        this.dateOfBirth = dateOfBirth;
        this.asn = asn;
        this.pncId = pncId;
        this.organisationName = organisationName;
        this.oicShoulderNumber = oicShoulderNumber;
        this.offences = offences;
    }

    public String getPartyId() {
        return partyId;
    }

    public List<String> get_party_type() {
        return _party_type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<AliasDocument> getAliases() {
        return aliases;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAsn() {
        return asn;
    }

    public String getPncId() {
        return pncId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getOicShoulderNumber() {
        return oicShoulderNumber;
    }

    public List<uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.OffenceDocument> getOffences() {
        return offences;
    }

    public static class Builder {

        private String partyId;
        private List<String> _party_type;
        private String firstName;
        private String lastName;
        private List<AliasDocument> aliases;
        private String dateOfBirth;
        private String asn;
        private String pncId;
        private String organisationName;
        private String oicShoulderNumber;
        private List<OffenceDocument> offences;


        public Builder withPartyId(final String partyId) {
            this.partyId = partyId;
            return this;
        }

        public Builder with_party_type(final List<String> _party_type) {
            this._party_type = _party_type;
            return this;
        }

        public Builder withFirstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withAliases(final List<AliasDocument> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder withDateOfBirth(final String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder withAsn(final String asn) {
            this.asn = asn;
            return this;
        }

        public Builder withPncId(final String pncId) {
            this.pncId = pncId;
            return this;
        }

        public Builder withOrganisationName(final String organisationName) {
            this.organisationName = organisationName;
            return this;
        }

        public Builder withOicShoulderNumber(final String oicShoulderNumber) {
            this.oicShoulderNumber = oicShoulderNumber;
            return this;
        }

        public Builder withOffences(final List<uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.OffenceDocument> offences) {
            this.offences = offences;
            return this;
        }


        public PartyDocument build() {
            return new PartyDocument(partyId, _party_type, firstName, lastName, aliases, dateOfBirth,
                    asn, pncId, organisationName, oicShoulderNumber, offences);
        }
    }
}

