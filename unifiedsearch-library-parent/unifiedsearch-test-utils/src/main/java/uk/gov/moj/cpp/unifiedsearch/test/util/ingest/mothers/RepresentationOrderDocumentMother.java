package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.RepresentationOrderDocument;

import java.util.concurrent.ThreadLocalRandom;

public class RepresentationOrderDocumentMother {

    public static RepresentationOrderDocument defaultRepresentationOrder() {
        return defaultRepresentationOrderAsBuilder().build();
    }


    public static RepresentationOrderDocument.Builder defaultRepresentationOrderAsBuilder() {

        final DateUtils dateUtils = new DateUtils();
        return new RepresentationOrderDocument.Builder()
                .withApplicationReference(randomApplicationReference())
                .withEffectiveFromDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withEffectiveToDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInNextThreeMonthsOrNull()))
                .withLaaContractNumber(randomLaaContractNumber());

    }


    private static String randomApplicationReference() {
        final int refNo = ThreadLocalRandom.current().nextInt(1, 999);
        return String.format("LAA REF %s", refNo);
    }

    private static String randomLaaContractNumber() {
        final int refNo = ThreadLocalRandom.current().nextInt(1, 999);
        return String.format("LAA CONTRACT NUMBER %s", refNo);
    }

}
