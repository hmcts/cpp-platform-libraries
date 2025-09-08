package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.PartyDocument;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartyDocumentMotherTest {

    @InjectMocks
    private PartyDocumentMother partyDocumentMother;

    @Test
    public void shouldCreateDefaultPartyList() {
        final List<PartyDocument> parties = partyDocumentMother.defaultPartyList();
        assertThat(parties, is(notNullValue()));
        for (final PartyDocument party : parties) {
            assertParty(party);
        }
    }

    @Test
    public void shouldCreateDefaultPartyBuildersList() {
        final List<PartyDocument.Builder> builders = partyDocumentMother.defaultPartyBuildersList();
        assertThat(builders, is(notNullValue()));
    }

    @Test
    public void shouldCreateDefaultParty() {
        assertParty(partyDocumentMother.defaultParty());
    }

    @Test
    public void shouldCreateDefaultPartyAsBuilder() {
        assertThat(partyDocumentMother.defaultPartyAsBuilder(), is(notNullValue()));
    }

    private void assertParty(final PartyDocument party) {
        assertThat(party.getPartyId(), is(notNullValue()));
        assertThat(party.get_party_type(), is(notNullValue()));
    }

}