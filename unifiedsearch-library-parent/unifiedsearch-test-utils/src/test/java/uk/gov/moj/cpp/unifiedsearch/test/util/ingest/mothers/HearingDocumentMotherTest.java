package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.HEARING_ESTIMATED_DURATION;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.defaultHearing;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.defaultHearingAsBuilder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.defaultHearings;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother.defaultHearingsAsBuilder;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.HearingDocument;

import java.util.List;

import org.junit.jupiter.api.Test;

public class HearingDocumentMotherTest {

    @Test
    public void shouldCreateDefaultHearing() {
        final HearingDocument hearing = defaultHearing();
        assertHearing(hearing);
    }


    @Test
    public void shouldCreateDefaultHearings() {
        final int count = 3;
        final List<HearingDocument> hearings = defaultHearings(count);
        for (final HearingDocument hearingDocument : hearings) {
            assertHearing(hearingDocument);
        }
    }

    @Test
    public void shouldCreateDefaultHearingsAsBuilder() {
        final int count = 7;
        final List<HearingDocument.Builder> builders = defaultHearingsAsBuilder(count);
        assertThat(builders, hasSize(count));
    }

    @Test
    public void shouldCreateDefaultHearingAsBuilder() {
        final HearingDocument.Builder builder = defaultHearingAsBuilder();
        assertThat(builder, is(notNullValue()));
    }


    private void assertHearing(final HearingDocument hearing) {
        assertThat(hearing.getHearingId(), is(notNullValue()));
        assertThat(hearing.getHearingTypeId(), is(notNullValue()));
        assertThat(hearing.getHearingTypeLabel(), is(notNullValue()));
        assertThat(hearing.getCourtId(), is(notNullValue()));
        assertThat(hearing.getCourtCentreName(), is(notNullValue()));
        assertThat(hearing.getHearingDates(), is(notNullValue()));
        assertThat(hearing.getHearingDays(), is(notNullValue()));
        assertThat(hearing.getHearingDays().size(), is(3));
        assertThat(hearing.getJurisdictionType(), is(notNullValue()));
        assertThat(hearing.getJudiciaryTypes(), is(notNullValue()));

        assertThat(hearing.getCourtCentreAddress(), is(notNullValue()));
        assertThat(hearing.getCourtCentreRoomId(), is(notNullValue()));
        assertThat(hearing.getCourtCentreRoomName(), is(notNullValue()));
        assertThat(hearing.getCourtCentreRoomWelshName(), is(notNullValue()));
        assertThat(hearing.getCourtCentreWelshName(), is(notNullValue()));
        assertThat(hearing.getHearingTypeCode(), is(notNullValue()));
        assertThat(hearing.getDefendantIds(), is(notNullValue()));
        assertThat(hearing.getDefendantIds(), hasSize(2));
        assertThat(hearing.getCourtCentreCode(), is(notNullValue()));
        assertNotNull(hearing.getEstimatedDuration());
        assertThat(hearing.getEstimatedDuration(), is(HEARING_ESTIMATED_DURATION));
        assertThat(hearing.getDefenceCounsels(), is(notNullValue()));
    }
}