package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateTimeUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.HearingDocument;

import java.util.ArrayList;
import java.util.List;

public class HearingDocumentMother {

    private static final DateTimeUtils DATE_TIME_UTILS = new DateTimeUtils();

    public static HearingDocument defaultHearing() {
        return defaultHearingAsBuilder().build();
    }


    public static List<HearingDocument> defaultHearings(final int count) {

        final List<HearingDocument> hearingList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            hearingList.add(defaultHearingAsBuilder().build());
        }

        return hearingList;
    }

    public static List<HearingDocument.Builder> defaultHearingsAsBuilder(final int count) {

        final List<HearingDocument.Builder> applicationBuilderList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            applicationBuilderList.add(defaultHearingAsBuilder());
        }

        return applicationBuilderList;
    }


    public static HearingDocument.Builder defaultHearingAsBuilder() {

        final HearingDocument.Builder builder = new HearingDocument.Builder();
        builder.withHearingId(randomUUID().toString())
                .withHearingDateTime(DATE_TIME_UTILS.randomDateTimeInTheNextMonthAsElasticsearchDateString())
                .withCourtHouse("London Crown Court")
                .withCourtRoom("CourtRoom 01")
                .withHearingType("Trial")
                .withJurisdiction("London");

        return builder;

    }

    public static List<HearingDocument> hearingDays(final String... hearingDateTimes) {
        final List<HearingDocument> hearing = new ArrayList();
        for (final String hearingDateTime : hearingDateTimes) {
            hearing.add(hearingDocumentAsBuilder(hearingDateTime));
        }

        return hearing;
    }

    public static HearingDocument hearingDocumentAsBuilder(final String hearingDateTime) {
        return new HearingDocument.Builder()
                .withHearingDateTime(DATE_TIME_UTILS.randomTimeForDay(hearingDateTime))
                .withHearingType("Trial")
                .withHearingId(randomUUID().toString())
                .withCourtHouse("London Crown Court")
                .withCourtRoom("CourtRoom 01")
                .withJurisdiction("London").build();
    }
}
