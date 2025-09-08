package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CourtOrderDocumentMother.defaultCourtOrder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.LaaReferenceDocumentMother.defaultLaaReference;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PleaDocumentMother.defaultPlea;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.VerdictDocumentMother.defaultVerdictDocumentMother;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.OffenceDocument;

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
                .withOffenceTitle(addRandomNumber("OFFENCE TITLE"))
                .withOffenceLegislation(addRandomNumber("OFFENCE LEGISLATION"))
                .withProceedingsConcluded(ThreadLocalRandom.current().nextBoolean())
                .withArrestDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withDateOfInformation(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withStartDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withEndDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withChargeDate(
                        dateUtils.toElasticsearchDateString(
                                dateUtils.randomDateInLastThreeMonths()))
                .withModeOfTrial("CROWN")
                .withOrderIndex(1)
                .withWording(addRandomNumber("OFFENCE WORDING"))
                .withLaaReference(defaultLaaReference())
                .withCourtOrders(asList(defaultCourtOrder()))
                .withPleas(asList(defaultPlea()))
                .withVerdict(defaultVerdictDocumentMother());
    }

    private static String addRandomNumber(final String source) {
        return format("%s %s", source, ThreadLocalRandom.current().nextInt(100, 999));
    }
}
