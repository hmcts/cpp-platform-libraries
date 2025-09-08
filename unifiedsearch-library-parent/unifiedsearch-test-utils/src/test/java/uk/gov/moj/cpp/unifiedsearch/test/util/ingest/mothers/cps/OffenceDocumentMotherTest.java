package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static uk.gov.justice.services.test.utils.core.matchers.UuidStringMatcher.isAUuid;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.OffenceDocument;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OffenceDocumentMotherTest {

    @InjectMocks
    private OffenceDocumentMother offenceDocumentMother;

    @InjectMocks
    private HearingDocumentMother hearingDocumentMother;

    @Test
    public void defaultOffenceDocument() {
        assertOffenceDocument(offenceDocumentMother.defaultOffenceDocument());
        hearingDocumentMother.defaultHearing();
    }

    @Test
    public void defaultOffenceDocumentAsBuilder() {
        final OffenceDocument.Builder builder = offenceDocumentMother.defaultOffenceDocumentAsBuilder();
        assertOffenceDocument(builder.build());
    }

    private void assertOffenceDocument(final OffenceDocument offenceDocument) {

        assertThat(offenceDocument, is(notNullValue()));
        assertThat(offenceDocument.getOffenceId(), isAUuid());
        assertThat(offenceDocument.getOffenceCode(), startsWith("OFFENCE CODE"));
    }
}