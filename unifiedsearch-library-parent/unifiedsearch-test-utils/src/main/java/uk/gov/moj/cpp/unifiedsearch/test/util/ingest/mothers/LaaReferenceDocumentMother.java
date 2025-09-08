package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.LaaReferenceDocument;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LaaReferenceDocumentMother {

    private static final List<String> STATUS_DESCRIPTION_LIST = asList(
            "LAA STATUS CODE 1", "LAA STATUS CODE 2", "LAA STATUS CODE 3", "LAA STATUS CODE 4", "LAA STATUS CODE 5"
    );

    public static LaaReferenceDocument defaultLaaReference() {
        return defaultLaaReferenceAsBuilder().build();
    }


    public static LaaReferenceDocument.Builder defaultLaaReferenceAsBuilder() {

        return new LaaReferenceDocument.Builder()
                        .withApplicationReference(randomApplicationReference())
                        .withStatusId(randomUUID().toString())
                        .withStatusCode(Integer.toString(ThreadLocalRandom.current().nextInt(100, 999)))
                        .withStatusDescription(randomLaaStatusDescription());

    }

    private static String randomLaaStatusDescription() {
        return STATUS_DESCRIPTION_LIST.get(ThreadLocalRandom.current().nextInt(0, 4));
    }


    private static String randomApplicationReference() {
        final int refNo = ThreadLocalRandom.current().nextInt(1, 999);
        return String.format("LAA REF %s", refNo);
    }
}
