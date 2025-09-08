package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.AliasDocumentMother.randomAliasList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.AliasDocument;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AliasDocumentMotherTest {

    @Test
    public void shouldCreateRandomAliasList() {
        final List<AliasDocument> aliases = randomAliasList();
        assertThat(aliases, is(notNullValue()));
    }

    @Test
    public void randomAlias() {
        final AliasDocument alias = AliasDocumentMother.randomAlias();
        assertThat(alias, is(notNullValue()));
    }
}