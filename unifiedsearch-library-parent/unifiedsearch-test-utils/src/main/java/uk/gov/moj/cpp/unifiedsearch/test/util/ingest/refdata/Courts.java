package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Courts {

    private static final int COURT_LIST_SIZE = 5;

    private static final List<Court> courts =
            asList(
                    new Court(randomUUID(), "London Crown Court"),
                    new Court(randomUUID(), "Birmingham Crown Court"),
                    new Court(randomUUID(), "Liverpool Magistrates Court"),
                    new Court(randomUUID(), "Exeter Crown Court"),
                    new Court(randomUUID(), "Brighton Magistrates Court")
            );


    public static Court randomCourt() {
        return courts.get(ThreadLocalRandom.current().nextInt(0, 4));
    }


}
