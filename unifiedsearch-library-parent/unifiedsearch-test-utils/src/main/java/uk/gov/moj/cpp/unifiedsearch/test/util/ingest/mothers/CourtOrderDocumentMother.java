package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CourtOrderDocument;

import java.util.concurrent.ThreadLocalRandom;

public class CourtOrderDocumentMother {

    public static CourtOrderDocument defaultCourtOrder() {
        return defaultCourtOrderAsBuilder().build();
    }

    public static CourtOrderDocument.Builder defaultCourtOrderAsBuilder() {

        final DateUtils dateUtils = new DateUtils();
        return new CourtOrderDocument.Builder()
                .withId(randomUUID().toString())
                .withOrderDate(dateUtils.toElasticsearchDateString(dateUtils.randomDateWithinFiveDaysOf(now())))
                .withStartDate(dateUtils.toElasticsearchDateString(dateUtils.randomDateWithinFiveDaysOf(now())))
                .withEndDate(dateUtils.toElasticsearchDateString(dateUtils.randomDateWithinThreeMonthsOf((now()))))
                .withLabel(randomCourtOrderLabel())
                .withOrderingHearingId(randomUUID().toString())
                .withJudicialResultTypeId(randomUUID().toString())
                .withIsSJPOrder(current().nextBoolean())
                .withCanBeSubjectOfBreachProceedings(current().nextBoolean())
                .withCanBeSubjectOfVariationProceedings(current().nextBoolean());
    }

    private static String randomCourtOrderLabel() {
        return String.format("Court Order Label %s", ThreadLocalRandom.current().nextInt(0, 10));
    }

}
