package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

import java.util.List;

public class PartyDocument {

    private String partyId;
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
    private String organisationName;
    private List<AliasDocument> aliases;
    private String nationalInsuranceNumber;
    private Boolean proceedingsConcluded;
    private RepresentationOrderDocument representationOrder;
    private List<OffenceDocument> offences;
    private String masterPartyId;
    private String croNumber;
    private String courtProceedingsInitiated;
    private AddressDocument defendantAddress;

    private PartyDocument() {
    }

    public PartyDocument(final String partyId, final String firstName, final String middleName, final String lastName,
                         final String title, final String dateOfBirth, final String gender, final String addressLines,
                         final String postCode, final String pncId, final String arrestSummonsNumber, final String _party_type,
                         final String organisationName, final List<AliasDocument> aliases, final String nationalInsuranceNumber,
                         final Boolean proceedingsConcluded, final RepresentationOrderDocument representationOrder,
                         final List<OffenceDocument> offences, final String masterPartyId, final String croNumber,
                         final String courtProceedingsInitiated, final AddressDocument defendantAddress) {
        this.partyId = partyId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.title = title;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.addressLines = addressLines;
        this.postCode = postCode;
        this.pncId = pncId;
        this.arrestSummonsNumber = arrestSummonsNumber;
        this._party_type = _party_type;
        this.organisationName = organisationName;
        this.aliases = aliases;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.proceedingsConcluded = proceedingsConcluded;
        this.representationOrder = representationOrder;
        this.offences = offences;
        this.croNumber = croNumber;
        this.courtProceedingsInitiated = courtProceedingsInitiated;
        this.masterPartyId = masterPartyId;
        this.defendantAddress = defendantAddress;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getAddressLines() {
        return addressLines;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getPncId() {
        return pncId;
    }

    public String getArrestSummonsNumber() {
        return arrestSummonsNumber;
    }

    public String get_party_type() {
        return _party_type;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public List<AliasDocument> getAliases() {
        return aliases;
    }

    public String getNationalInsuranceNumber() {
        return nationalInsuranceNumber;
    }

    public Boolean isProceedingsConcluded() {
        return proceedingsConcluded;
    }

    public RepresentationOrderDocument getRepresentationOrder() {
        return representationOrder;
    }

    public List<OffenceDocument> getOffences() {
        return offences;
    }

    public String getMasterPartyId() {
        return masterPartyId;
    }

    public String getCroNumber() {
        return croNumber;
    }

    public String getCourtProceedingsInitiated() {
        return courtProceedingsInitiated;
    }

    public AddressDocument getDefendantAddress() {
        return defendantAddress;
    }


    public static class Builder {

        private String partyId;
        private String firstName;
        private String middleName;
        private String firstNameK;
        private String lastName;
        private String lastNameK;
        private String title;
        private String dateOfBirth;
        private String gender;
        private String addressLines;
        private String postCode;
        private String pncId;
        private String arrestSummonsNumber;
        private String _party_type;
        private String organisationName;
        private List<AliasDocument> aliases;

        private String nationalInsuranceNumber;
        private Boolean proceedingsConcluded;
        private RepresentationOrderDocument representationOrder;
        private List<OffenceDocument> offences;
        private String masterPartyId;
        private String croNumber;
        private String courtProceedingsInitiated;
        private AddressDocument defendantAddress;

        public Builder withPartyId(final String partyId) {
            this.partyId = partyId;
            return this;
        }

        public Builder withFirstName(final String firstName) {
            this.firstName = firstName;
            this.firstNameK = firstName;
            return this;
        }

        public Builder withMiddleName(final String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = lastName;
            this.lastNameK = lastName;
            return this;
        }

        public Builder withTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder withDateOfBirth(final String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder withGender(final String gender) {
            this.gender = gender;
            return this;
        }

        public Builder withAddressLines(final String addressLines) {
            this.addressLines = addressLines;
            return this;
        }

        public Builder withPostCode(final String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Builder withPncId(final String pncId) {
            this.pncId = pncId;
            return this;
        }

        public Builder withArrestSummonsNumber(final String arrestSummonsNumber) {
            this.arrestSummonsNumber = arrestSummonsNumber;
            return this;
        }

        public Builder withPartyType(final String _party_type) {
            this._party_type = _party_type;
            return this;
        }

        public Builder withOrganisationName(final String organisationName) {
            this.organisationName = organisationName;
            return this;
        }

        public Builder withAliases(final List<AliasDocument> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder withNationalInsuranceNumber(final String nationalInsuranceNumber) {
            this.nationalInsuranceNumber = nationalInsuranceNumber;
            return this;
        }

        public Builder withProceedingsConcluded(final Boolean proceedingsConcluded) {
            this.proceedingsConcluded = proceedingsConcluded;
            return this;
        }

        public Builder withRepresentationOrder(final RepresentationOrderDocument representationOrder) {
            this.representationOrder = representationOrder;
            return this;
        }

        public Builder withOffences(final List<OffenceDocument> offences) {
            this.offences = offences;
            return this;
        }

        public Builder withCroNumber(final String croNumber) {
            this.croNumber = croNumber;
            return this;
        }

        public Builder withMasterPartyId(final String masterPartyId) {
            this.masterPartyId = masterPartyId;
            return this;
        }


        public Builder withCourtProceedingsInitiated(final String courtProceedingsInitiated) {
            this.courtProceedingsInitiated = courtProceedingsInitiated;
            return this;
        }

        public Builder withDefendantAddress(final AddressDocument defendantAddress) {
            this.defendantAddress = defendantAddress;
            return this;
        }


        public PartyDocument build() {
            return new PartyDocument(partyId, firstName, middleName, lastName, title, dateOfBirth,
                    gender, addressLines, postCode, pncId, arrestSummonsNumber,
                    _party_type, organisationName, aliases, nationalInsuranceNumber,
                    proceedingsConcluded, representationOrder, offences, masterPartyId,
                    croNumber, courtProceedingsInitiated, defendantAddress);
        }
    }
}
