package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.HearingDayDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.HearingDayDocument.Builder;

import java.util.List;

import org.junit.jupiter.api.Test;

public class HearingDayDocumentMotherTest {

    @Test
    public void defaultHearingDayDocument() {
        assertHearingDayDocument(HearingDayDocumentMother.defaultHearingDayDocument());
    }

    @Test
    public void defaultHearingDayDocuments() {
        final List<HearingDayDocument> hearingDayDocuments = HearingDayDocumentMother.defaultHearingDayDocuments();
        assertNotNull(hearingDayDocuments);
        assertEquals(hearingDayDocuments.size(), 3);
        hearingDayDocuments.forEach(this::assertHearingDayDocument);

    }

    @Test
    public void defaultHearingDayDocumentAsBuilder() {
        final Builder defaultHearingDayDocumentAsBuilder = HearingDayDocumentMother.defaultHearingDayDocumentAsBuilder();

        assertNotNull(defaultHearingDayDocumentAsBuilder);
        assertHearingDayDocument(defaultHearingDayDocumentAsBuilder.build());
    }
    @Test
    public void hearingDays() {
        final List<HearingDayDocument> haringDays = HearingDayDocumentMother.hearingDays("2020-02-02", "1802-12-12");

        assertThat(haringDays, hasSize(2));
        assertHeatingDay(haringDays.get(0), "2020-02-02");
        assertHeatingDay(haringDays.get(1), "1802-12-12");

    }

    protected void assertHeatingDay(final HearingDayDocument haringDay, final String sittingDay) {
        assertThat(haringDay.getSittingDay().substring(0, 10), is(sittingDay));
        assertThat(haringDay.getListingSequence(), is(greaterThan(0)));
        assertThat(haringDay.getListedDurationMinutes(), is(greaterThan(0)));
        assertThat(haringDay.getHasSharedResults().booleanValue(), is(true));
    }

    private void assertHearingDayDocument(final HearingDayDocument document) {
        assertEquals(document.getListedDurationMinutes(), 60);
        assertTrue(document.getListingSequence() > 0);
        assertNotNull(document.getSittingDay());
        assertNotNull(document.getCourtCentreId());
        assertNotNull(document.getCourtRoomId());
        assertTrue(document.getHasSharedResults());

    }

    @Test
    public void defaultHearingDayDocumentsAsBuilder() {
        final List<Builder> builders = HearingDayDocumentMother.defaultHearingDayDocumentsAsBuilder();
        assertNotNull(builders);
        assertEquals(builders.size(), 3);
        builders.stream()
                .map(Builder::build)
                .forEach(this::assertHearingDayDocument);
    }
}