package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;


import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.ApplicationExternalCreatorType.PROSECUTOR;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.SubjectDocumentMother.defaultSubjectDocumentMother;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.ApplicationStatus;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.ApplicationDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata.ApplicationType;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata.ApplicationTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ObjectMother for {@link ApplicationDocument} creation
 */
public class ApplicationDocumentMother {

    private static final DateUtils DATE_UTILS = new DateUtils();

    private ApplicationDocumentMother() {
        throw new IllegalStateException("Utility class");
    }

    public static ApplicationDocument defaultApplication() {
        return defaultApplicationAsBuilder().build();
    }

    public static List<ApplicationDocument> defaultApplications(final int count) {

        final List<ApplicationDocument> applicationList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            applicationList.add(defaultApplicationAsBuilder().build());
        }

        return applicationList;
    }

    public static List<ApplicationDocument.Builder> defaultApplicationsAsBuilder(final int count) {

        final List<ApplicationDocument.Builder> applicationBuilderList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            applicationBuilderList.add(defaultApplicationAsBuilder());
        }

        return applicationBuilderList;
    }

    public static ApplicationDocument.Builder defaultApplicationAsBuilder() {

        final ApplicationType type = ApplicationTypes.randomApplicationType();
        final LocalDate receivedDate = DATE_UTILS.randomDateInLastThreeMonths();

        final ApplicationDocument.Builder builder = new ApplicationDocument.Builder();
        builder.withApplicationId(randomUUID().toString())
                .withApplicationReference(type.getApplicationCode())
                .withApplicationType(type.getApplicationType())
                .withApplicationTypeCode(type.getApplicationCode())
                .withReceivedDate(DATE_UTILS.toElasticsearchDateString(receivedDate))
                .withDecisionDate(
                        DATE_UTILS.toElasticsearchDateString(
                                DATE_UTILS.randomDateWithinThreeMonthsOf(receivedDate)))
                .withDueDate(
                        DATE_UTILS.toElasticsearchDateString(
                                DATE_UTILS.randomDateWithinThreeMonthsOf(receivedDate)))
                .withApplicationStatus(randomApplicationStatus())
                .withApplicationExternalCreatorType(PROSECUTOR.name())
                .withSubjectSummary(defaultSubjectDocumentMother());

        return builder;
    }

    private static String randomApplicationStatus() {
        final int index = current().nextInt(0, 5);
        return ApplicationStatus.values()[index].name();
    }

}
