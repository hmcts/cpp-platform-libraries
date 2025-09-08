package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.AliasDocument;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AliasDocumentMotherTest {

    @InjectMocks
    private AliasDocumentMother aliasDocumentMother;

    @Test
    public void shouldCreateRandomAliasList() {
        final List<AliasDocument> aliases = aliasDocumentMother.randomAliasList();
        assertThat(aliases, is(notNullValue()));
    }

    @Test
    public void randomAlias() {
        final AliasDocument alias = aliasDocumentMother.randomAlias();
        assertThat(alias, is(notNullValue()));
    }
}