package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.DefenceCounselMother.defaultDefenceCounselDocuments;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDayDocumentMother.defaultHearingDayDocuments;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomAddresses.randomAddressObject;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.HearingDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata.Court;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata.Courts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class HearingDocumentMother {


    private static List<UUID> hearingTypeIdList = asList(
            randomUUID(), randomUUID(), randomUUID(), randomUUID(), randomUUID()
    );


    private static List<UUID> jurisdictionTypeIdList = asList(
            randomUUID(), randomUUID(), randomUUID(), randomUUID(), randomUUID()
    );


    private static List<UUID> judiciaryTypes = asList(
            randomUUID(), randomUUID(), randomUUID(), randomUUID(), randomUUID()
    );

    private static List<UUID> courtCentreRoomIds = asList(
            randomUUID(), randomUUID(), randomUUID(), randomUUID(), randomUUID()
    );

    private static List<String> courtNames = asList(
            "Courtroom 1", "Courtroom 2", "Courtroom 3", "Courtroom 4", "Courtroom 5", "Courtroom 6"
    );

    private static List<String> hearingTypeCodes = asList(
            "HEARING TYPE CODE 1", "HEARING TYPE CODE  2", "HEARING TYPE CODE  3", "HEARING TYPE CODE  4",
            "HEARING TYPE CODE  5", "HEARING TYPE CODE  6"
    );

    private static List<String> courtCentreCodes = asList(
            "COURT CENTRE CODE 1", "COURT CENTRE CODE  2", "COURT CENTRE CODE  3", "COURT CENTRE CODE  4",
            "COURT CENTRE CODE  5", "COURT CENTRE CODE  6"
    );

    public static final String HEARING_ESTIMATED_DURATION = "20 Minutes";

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

        final Court court = Courts.randomCourt();

        final HearingDocument.Builder builder = new HearingDocument.Builder();
        builder.withHearingId(randomUUID().toString())
                .withCourtId(court.getCourtId().toString())
                .withCourtCentreName(court.getCourtCentreName())
                .withHearingTypeId(randomHearingTypeId())
                .withHearingTypLabel("Trial")
                .withHearingDays(defaultHearingDayDocuments())
                .withJurisdictionType(randomJurisdictionTypeId())
                .withJudiciaryTypes(randomJudiciaryTypes())
                .withEstimatedDuration(HEARING_ESTIMATED_DURATION);

        if (ThreadLocalRandom.current().nextBoolean()) {
            builder.withBoxHearing(true);
            builder.withVirtualBoxHearing(true);
        }

        builder.withCourtCentreAddress(randomAddressObject());
        builder.withCourtCentreRoomId(randomCourtCentreRoomId());
        builder.withCourtCentreRoomName(randomCourtName());
        builder.withCourtCentreRoomWelshName(randomCourtName());
        builder.withCourtCentreWelshName(randomCourtName());
        builder.withHearingTypeCode(randomHearingTypeCode());
        builder.withDefendantIds(asList(randomUUID().toString(), randomUUID().toString()));
        builder.withCourtCentreCode(randomCourCentreCode());
        builder.withDefenceCounsels(defaultDefenceCounselDocuments());

        return builder;

    }

    private static String randomHearingTypeId() {
        return hearingTypeIdList.get(ThreadLocalRandom.current().nextInt(0, 4)).toString();
    }

    private static String randomJurisdictionTypeId() {
        return jurisdictionTypeIdList.get(ThreadLocalRandom.current().nextInt(0, 4)).toString();
    }

    private static List<String> randomJudiciaryTypes() {
        final List<String> judiciaryTypeList = new ArrayList<>();

        final int numOfTypes = ThreadLocalRandom.current().nextInt(0, 2);

        for (int i = 0; i < numOfTypes; i++) {
            judiciaryTypeList.add(judiciaryTypes.get(ThreadLocalRandom.current().nextInt(0, 2)).toString());
        }

        return judiciaryTypeList;
    }

    private static String randomCourtCentreRoomId() {
        return courtCentreRoomIds.get(ThreadLocalRandom.current().nextInt(0, 4)).toString();
    }

    private static String randomCourtName() {
        return courtNames.get(ThreadLocalRandom.current().nextInt(0, 5)).toString();
    }

    private static String randomHearingTypeCode() {
        return hearingTypeCodes.get(ThreadLocalRandom.current().nextInt(0, 5)).toString();
    }

    private static String randomCourCentreCode() {
        return courtCentreCodes.get(ThreadLocalRandom.current().nextInt(0, 5)).toString();
    }
}
