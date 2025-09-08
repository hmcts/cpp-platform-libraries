package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.OffenceDocument;

import java.util.concurrent.ThreadLocalRandom;

public class OffenceDocumentMother {



    public static OffenceDocument defaultOffenceDocument() {
        return defaultOffenceDocumentAsBuilder().build();
    }


    public static OffenceDocument.Builder defaultOffenceDocumentAsBuilder() {

        final DateUtils dateUtils = new DateUtils();

        return new OffenceDocument.Builder()
                .withOffenceId(randomUUID().toString())
                .withOffenceCode(addRandomNumber("OFFENCE CODE"))
                .withCustodyTimeLimit(dateUtils.toElasticsearchDateString(
                        dateUtils.randomDateInLastThreeMonths()))
                .withType("OFFENCE");
    }

    private static String addRandomNumber(final String source) {
        return format("%s %s", source, ThreadLocalRandom.current().nextInt(100, 999));
    }
}
