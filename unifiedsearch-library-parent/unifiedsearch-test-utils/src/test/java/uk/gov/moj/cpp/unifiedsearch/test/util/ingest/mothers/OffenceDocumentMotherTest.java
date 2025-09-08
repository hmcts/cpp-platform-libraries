package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static uk.gov.justice.services.test.utils.core.matchers.UuidStringMatcher.isAUuid;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.SimpleDateStringMatcher.isADateString;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.defaultHearing;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.OffenceDocument;

import org.junit.jupiter.api.Test;

public class OffenceDocumentMotherTest {

    @Test
    public void defaultOffenceDocument() {
        assertOffenceDocument(OffenceDocumentMother.defaultOffenceDocument());
        defaultHearing();
    }

    @Test
    public void defaultOffenceDocumentAsBuilder() {
        final OffenceDocument.Builder builder = OffenceDocumentMother.defaultOffenceDocumentAsBuilder();
        assertOffenceDocument(builder.build());
    }

    private void assertOffenceDocument(final OffenceDocument offenceDocument) {

        assertThat(offenceDocument, is(notNullValue()));
        assertThat(offenceDocument.getOffenceId(), isAUuid());
        assertThat(offenceDocument.getOffenceCode(), startsWith("OFFENCE CODE"));
        assertThat(offenceDocument.getOffenceTitle(), startsWith("OFFENCE TITLE"));
        assertThat(offenceDocument.getOffenceLegislation(), startsWith("OFFENCE LEGISLATION"));
        assertThat(offenceDocument.getArrestDate(), isADateString());
        assertThat(offenceDocument.getDateOfInformation(), isADateString());
        assertThat(offenceDocument.getStartDate(), isADateString());
        assertThat(offenceDocument.getEndDate(), isADateString());
        assertThat(offenceDocument.getChargeDate(), isADateString());
        assertThat(offenceDocument.getModeOfTrial(), is("CROWN"));
        assertThat(offenceDocument.getWording(), startsWith("OFFENCE WORDING"));
        assertThat(offenceDocument.getLaaReference(), is(notNullValue()));
        assertThat(offenceDocument.getCourtOrders(), hasSize(greaterThanOrEqualTo(1)));
        assertThat(offenceDocument.getPleas(), hasSize(greaterThanOrEqualTo(1)));
        assertThat(offenceDocument.getVerdict().getVerdictDate(), is("2019-01-01"));
        assertThat(offenceDocument.getVerdict().getVerdictType().getCategory(), is("Category"));

    }
}