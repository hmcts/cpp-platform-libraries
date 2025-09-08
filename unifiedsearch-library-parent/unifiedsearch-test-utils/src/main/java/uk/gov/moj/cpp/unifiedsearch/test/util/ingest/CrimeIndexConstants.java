package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.ElasticSearchTestHostProvider.getUri;

public class CrimeIndexConstants {

    static final String ES_CRIME_CASE_INDEX_NAME = "crime_case_index";

    static final String ES_URI = getUri();

    private CrimeIndexConstants() {
    }

}
