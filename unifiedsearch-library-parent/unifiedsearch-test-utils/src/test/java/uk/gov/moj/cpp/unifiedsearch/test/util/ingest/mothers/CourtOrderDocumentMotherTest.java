package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static uk.gov.justice.services.test.utils.core.matchers.UuidStringMatcher.isAUuid;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.SimpleDateStringMatcher.isADateString;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CourtOrderDocumentMother.defaultCourtOrder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CourtOrderDocumentMother.defaultCourtOrderAsBuilder;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CourtOrderDocument;

import org.junit.jupiter.api.Test;

public class CourtOrderDocumentMotherTest {

    @Test
    public void shouldCreateDefaultCourtOrder() {
        assertCourOrderDocument(defaultCourtOrder());
    }

    @Test
    public void shouldCreateDefaultCourtOrderAsBuilder() {
        assertCourOrderDocument(defaultCourtOrderAsBuilder().build());
    }

    private void assertCourOrderDocument(final CourtOrderDocument courtOrderDocument) {

        assertThat(courtOrderDocument, is(notNullValue()));
        assertThat(courtOrderDocument.getId(), isAUuid());
        assertThat(courtOrderDocument.getLabel(), startsWith("Court Order Label"));
        assertThat(courtOrderDocument.getJudicialResultTypeId(), isAUuid());
        assertThat(courtOrderDocument.getOrderDate(), isADateString());
        assertThat(courtOrderDocument.getStartDate(), isADateString());
        assertThat(courtOrderDocument.getEndDate(), isADateString());

        assertThat(courtOrderDocument.getOrderingHearingId(), isAUuid());
        assertThat(courtOrderDocument.getCanBeSubjectOfBreachProceedings(), is(notNullValue()));
        assertThat(courtOrderDocument.getCanBeSubjectOfVariationProceedings(), is(notNullValue()));
        assertThat(courtOrderDocument.getIsSJPOrder(), is(notNullValue()));
    }
}