package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother.defaultParty;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother.defaultPartyAsBuilder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother.defaultPartyBuildersList;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother.defaultPartyList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.PartyDocument;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PartyDocumentMotherTest {

    @Test
    public void shouldCreateDefaultPartyList() {
        final List<PartyDocument> parties = defaultPartyList();
        assertThat(parties, is(notNullValue()));
        for (final PartyDocument party : parties) {
            assertParty(party);
        }
    }

    @Test
    public void shouldCreateDefaultPartyBuildersList() {
        final List<PartyDocument.Builder> builders = defaultPartyBuildersList();
        assertThat(builders, is(notNullValue()));
    }

    @Test
    public void shouldCreateDefaultParty() {
        assertParty(defaultParty());
    }

    @Test
    public void shouldCreateDefaultPartyAsBuilder() {
        assertThat(defaultPartyAsBuilder(), is(notNullValue()));
    }

    private void assertParty(final PartyDocument party) {
        assertThat(party.getPartyId(), is(notNullValue()));
        assertThat(party.get_party_type(), is(notNullValue()));
    }

}