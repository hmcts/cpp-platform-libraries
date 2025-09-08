
package uk.gov.justice.services.unifiedsearch.client.domain;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class PartyTest {

    @Test
    public void shouldCreateFrom() {

        final Party sourceParty = party();
        final Party clone = Party.from(sourceParty);

        assertEquals(sourceParty, clone);
    }
    
    private Party party() {
        final Party sourceParty = new Party();

        sourceParty.setPartyId("ID 1");
        sourceParty.setFirstName("First Name");
        sourceParty.setMiddleName("Middle Name");
        sourceParty.setLastName("Las Name");
        sourceParty.setTitle("Title");
        sourceParty.setDateOfBirth("1980-06-06");
        sourceParty.setGender("Male");
        sourceParty.setAddressLines("1 Alpha Avenue Croydon");
        sourceParty.setPostCode("CR01 7TT");
        sourceParty.setPncId("21");
        sourceParty.setArrestSummonsNumber("121");
        sourceParty.set_party_type("SUSPECT");
        sourceParty.setAliases(new HashSet<>(asList(
                alias("MR", "Alan", null, "Aardvark", null),
                alias(null, "Boris", null, "Balmy", null),
                alias("MRS", "Colin", null, "Cardsup", null)
        )));
        sourceParty.setOrganisationName("Org Name");
        sourceParty.setNationalInsuranceNumber("DEFENDANT_NINO");
        sourceParty.setProceedingsConcluded(false);
        sourceParty.setCroNumber("croNumber");
        sourceParty.setMasterPartyId("6beaf636-e370-42dd-b2eb-c92a688c20b5");
        sourceParty.setCourtProceedingsInitiated("1980-06-06:10:20:12Z");

        sourceParty.setRepresentationOrder(representationOrder());

        sourceParty.setOffences(asList(offence(),  offence()));
        sourceParty.setDefendantAddress(defendantAddress());
        return sourceParty;
    }

    private Alias alias(final String title, final String firstName, final String middleName, final String lastName, final String orgName) {
        final Alias alias = new Alias();
        ofNullable(title).ifPresent(name -> alias.setTitle(title));
        ofNullable(firstName).ifPresent(name -> alias.setFirstName(name));
        ofNullable(middleName).ifPresent(name -> alias.setMiddleName(name));
        ofNullable(lastName).ifPresent(name -> alias.setLastName(name));
        ofNullable(orgName).ifPresent(name -> alias.setOrganisationName(name));
        return alias;
    }

    private RepresentationOrder representationOrder() {
        final RepresentationOrder representationOrder = new RepresentationOrder();
        representationOrder.setApplicationReference("LAA APP REF");
        representationOrder.setEffectiveFromDate("2019-10-10");
        representationOrder.setEffectiveToDate("2019-10-10");
        representationOrder.setLaaContractNumber("123");
        return representationOrder;
    }

    private Offence offence() {
        final Offence offence = new Offence();

        offence.setOffenceId(randomUUID().toString());

        return offence;
    }

    private Address defendantAddress() {
        final Address defendantAddress = new Address();
        defendantAddress.setAddress1("address1");
        defendantAddress.setAddress2("address2");
        defendantAddress.setAddress3("address3");
        defendantAddress.setAddress4("address4");
        defendantAddress.setAddress5("address5");
        defendantAddress.setPostCode("postCode");
        return defendantAddress;
    }
}