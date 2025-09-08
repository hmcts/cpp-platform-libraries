package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static uk.gov.justice.services.test.utils.core.matchers.UuidStringMatcher.isAUuid;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.LaaReferenceDocument;

import org.junit.jupiter.api.Test;

public class LaaReferenceDocumentMotherTest {

    @Test
    public void defaultLaaReference() {
        final LaaReferenceDocument laaReferenceDocument = LaaReferenceDocumentMother.defaultLaaReference();
        assertLaaReferenceDocument(laaReferenceDocument);
    }

    @Test
    public void defaultLaaReferenceAsBuilder() {
        final LaaReferenceDocument.Builder builder = LaaReferenceDocumentMother.defaultLaaReferenceAsBuilder();
        assertLaaReferenceDocument(builder.build());
    }

    private void assertLaaReferenceDocument(final LaaReferenceDocument laaReferenceDocument) {
        assertThat(laaReferenceDocument, is(notNullValue()));
        assertThat(laaReferenceDocument.getApplicationReference(), startsWith("LAA REF"));
        assertThat(laaReferenceDocument.getStatusId(), isAUuid());
        assertThat(laaReferenceDocument.getStatusCode(), is(notNullValue()));
        assertThat(laaReferenceDocument.getStatusDescription(), startsWith("LAA STATUS CODE"));
    }



}