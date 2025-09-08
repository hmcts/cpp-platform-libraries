package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.HearingDocument;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HearingDocumentMotherTest {

    @InjectMocks
    private HearingDocumentMother hearingDocumentMother;

    @Test
    public void shouldCreateDefaultHearing() {
        final HearingDocument hearing = hearingDocumentMother.defaultHearing();
        assertHearing(hearing);
    }


    @Test
    public void shouldCreateDefaultHearings() {
        final int count = 3;
        final List<HearingDocument> hearings = hearingDocumentMother.defaultHearings(count);
        for (final HearingDocument hearingDocument : hearings) {
            assertHearing(hearingDocument);
        }
    }

    @Test
    public void shouldCreateDefaultHearingsAsBuilder() {
        final int count = 7;
        final List<HearingDocument.Builder> builders = hearingDocumentMother.defaultHearingsAsBuilder(count);
        assertThat(builders, hasSize(count));
    }

    @Test
    public void shouldCreateDefaultHearingAsBuilder() {
        final HearingDocument.Builder builder = hearingDocumentMother.defaultHearingAsBuilder();
        assertThat(builder, is(notNullValue()));
    }


    private void assertHearing(final HearingDocument hearing) {
        assertThat(hearing.getHearingId(), is(notNullValue()));
        assertThat(hearing.getHearingDateTime(), is(notNullValue()));
        assertThat(hearing.getCourtHouse(), is(notNullValue()));
        assertThat(hearing.getCourtRoom(), is(notNullValue()));
        assertThat(hearing.getHearingType(), is(notNullValue()));
        assertThat(hearing.getJurisdiction(), is(notNullValue()));
    }

    @Test
    public void hearingDays() {
        final List<HearingDocument> hearingDays = HearingDocumentMother.hearingDays("2020-02-02", "1802-12-12");

        assertThat(hearingDays, Matchers.hasSize(2));
        assertThat(hearingDays.get(0).getHearingDateTime().substring(0,10),is("2020-02-02"));
        assertThat(hearingDays.get(1).getHearingDateTime().substring(0,10),is("1802-12-12"));

    }
}