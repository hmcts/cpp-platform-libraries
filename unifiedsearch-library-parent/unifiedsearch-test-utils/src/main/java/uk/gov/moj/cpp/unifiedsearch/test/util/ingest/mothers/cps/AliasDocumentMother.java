package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomFirstName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomLastName;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.AliasDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class AliasDocumentMother {

    public static List<AliasDocument> randomAliasList() {
        final List<AliasDocument> aliasList = new ArrayList<>();

        if (ThreadLocalRandom.current().nextBoolean()) {

            final int count = ThreadLocalRandom.current().nextInt(0, 2);
            for (int i = 0; i < count; i++) {
                aliasList.add(randomAlias());
            }
        }

        return aliasList;
    }


    public static AliasDocument randomAlias() {
        return new AliasDocument(randomFirstName(), randomLastName());
    }
}
