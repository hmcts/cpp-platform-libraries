package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.VerdictDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.VerdictTypeDocument;

public class VerdictDocumentMother {

    public static VerdictDocument defaultVerdictDocumentMother(){
        return new VerdictDocument.Builder()
                .withVerdictDate("2019-01-01")
                .withOriginatingHearingId(randomUUID().toString())
                .withVerdictType(new VerdictTypeDocument.Builder()
                        .withCategory("Category")
                        .withCategoryType("CategoryType")
                        .withDescription("Description")
                        .withSequence(1)
                        .withVerdictTypeId(randomUUID().toString())
                        .build())
                .build();
    }
}
