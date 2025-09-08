package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.DefenceCounselDocument;

public class DefenceCounselMother {


    public static List<DefenceCounselDocument> defaultDefenceCounselDocuments() {

        return Arrays.asList(new DefenceCounselDocument.Builder().withId("2ffa8961-58a3-4352-a87d-09aa45f68c7f")
                .withTitle("Mr")
                .withFirstName("johnny")
                .withLastName("robber")
                .withDefendants(asList("2ffa8961-58a3-4352-a87d-09aa45f68c7f", "2ffa8961-58a3-4352-a87d-09aa45f68c8f"))
                .withAttendanceDays(asList("2019-01-01"))
                .build());
    }
}
