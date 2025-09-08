package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.asList;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateTimeUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.HearingDayDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HearingDayDocumentMother {

    private static final DateTimeUtils DATE_TIME_UTILS = new DateTimeUtils();

    public static HearingDayDocument defaultHearingDayDocument() {
        return defaultHearingDayDocumentAsBuilder().build();
    }

    public static List<HearingDayDocument> defaultHearingDayDocuments() {

        return defaultHearingDayDocumentsAsBuilder().stream()
                .map(HearingDayDocument.Builder::build)
                .collect(toList());

    }


    public static HearingDayDocument.Builder defaultHearingDayDocumentAsBuilder() {

        return new HearingDayDocument.Builder()
                .withSittingDay(DATE_TIME_UTILS.randomDateTimeInTheNextMonthAsElasticsearchDateString())
                .withListedDurationMinutes(60)
                .withCourtCentreId(UUID.randomUUID().toString())
                .withCourtRoomId(UUID.randomUUID().toString())
                .withHasSharedResults(true)
                .withListingSequence(1);
    }

    public static HearingDayDocument.Builder hearingDayDocumentAsBuilder(final String sittingDay) {
        return new HearingDayDocument.Builder()
                .withSittingDay(DATE_TIME_UTILS.randomTimeForDay(sittingDay))
                .withListedDurationMinutes(current().nextInt(1, 999))
                .withHasSharedResults(true)
                .withListingSequence(current().nextInt(1, 50));
    }

    public static HearingDayDocument.Builder hearingDayDocumentAsBuilder(final String sittingDay,final UUID courtCentreId,final UUID  courtRoomId) {
        return new HearingDayDocument.Builder()
                .withSittingDay(DATE_TIME_UTILS.randomTimeForDay(sittingDay))
                .withListedDurationMinutes(current().nextInt(1, 999))
                .withListingSequence(current().nextInt(1, 50))
                .withCourtCentreId(courtCentreId.toString())
                .withHasSharedResults(true)
                .withCourtRoomId(courtRoomId.toString());
    }

    public static List<HearingDayDocument> hearingDays( final String... sittingDays) {
        final List<HearingDayDocument> hearingDays = new ArrayList();
        for (final String sittingDay : sittingDays) {
            hearingDays.add(hearingDay(sittingDay));
        }

        return hearingDays;
    }



    public static HearingDayDocument hearingDay(final String sittingDay,final UUID courtCentreId,final UUID courtRoomId ) {
        return hearingDayDocumentAsBuilder(sittingDay,courtCentreId,courtRoomId).build();
    }



    private static HearingDayDocument hearingDay(final String sittingDay) {
        return hearingDayDocumentAsBuilder(sittingDay).build();
    }

    public static List<HearingDayDocument.Builder> defaultHearingDayDocumentsAsBuilder() {
        final List<String> dateTimeSequentialList = DATE_TIME_UTILS.randomSequentialDateRangeAsElasticsearchDateString();
        return asList(
                defaultHearingDayDocumentAsBuilder().withSittingDay(dateTimeSequentialList.get(0)).withListingSequence(1).withCourtCentreId(UUID.randomUUID().toString()).withCourtRoomId(UUID.randomUUID().toString()),
                defaultHearingDayDocumentAsBuilder().withSittingDay(dateTimeSequentialList.get(1)).withListingSequence(2).withCourtCentreId(UUID.randomUUID().toString()).withCourtRoomId(UUID.randomUUID().toString()),
                defaultHearingDayDocumentAsBuilder().withSittingDay(dateTimeSequentialList.get(2)).withListingSequence(3).withCourtCentreId(UUID.randomUUID().toString()).withCourtRoomId(UUID.randomUUID().toString()));

    }

    public static List<HearingDayDocument> hearingDays(final UUID courtCentreId, final String... sittingDays) {
        final List<HearingDayDocument> hearingDays = new ArrayList();
        for (final String sittingDay : sittingDays) {
            hearingDays.add(hearingDay(sittingDay, courtCentreId, UUID.randomUUID()));
        }

        return hearingDays;
    }

    public static List<HearingDayDocument> hearingDays(final UUID courtCentreId) {
        return of(defaultHearingDayDocumentAsBuilder().withCourtCentreId(courtCentreId.toString()).build());
    }
}
