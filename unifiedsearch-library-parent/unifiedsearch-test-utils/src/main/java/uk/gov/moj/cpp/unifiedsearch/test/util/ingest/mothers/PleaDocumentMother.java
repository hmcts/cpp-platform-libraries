package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.PleaDocument;

import java.util.concurrent.ThreadLocalRandom;

public class PleaDocumentMother {

    public static PleaDocument defaultPlea() {
        return defaultPleaAsBuilder().build();
    }

    public static PleaDocument.Builder defaultPleaAsBuilder() {
        DateUtils dateUtils = new DateUtils();

        return new PleaDocument.Builder()
                .withOriginatingHearingId(randomUUID().toString())
                .withPleaValue(randomPleaValue())
                .withPleaDate(dateUtils.toElasticsearchDateString(dateUtils.randomDateWithinFiveDaysOf(now())));
    }

    private static String randomPleaValue() {
        return String.format("Plea value %s", ThreadLocalRandom.current().nextInt(0, 10));
    }
}
