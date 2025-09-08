package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomFirstName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomLastName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomMiddleName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomOrganisationName;

import org.junit.jupiter.api.Test;

public class RandomNamesTest {

    @Test
    public void shouldCreateRandomFirstName() {
        assertThat(randomFirstName(), is(notNullValue()));
    }

    @Test
    public void shouldCreateRandomMiddleName() {
        assertThat(randomMiddleName(), is(notNullValue()));

    }

    @Test
    public void shouldCreateRandomLastName() {
        assertThat(randomLastName(), is(notNullValue()));

    }

    @Test
    public void shouldCreateRandomOrganisationName() {
        assertThat(randomOrganisationName(), is(notNullValue()));
    }
}