package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.LinkedCaseDocument;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinkedCaseDocumentMotherTest {

    @InjectMocks
    private LinkedCaseDocumentMother linkedCaseDocumentMother;

    @Test
    public void shouldCreateDefaultHearing() {
        final LinkedCaseDocument linkedCase = linkedCaseDocumentMother.defaultLinkedCase();
        assertLinkedCase(linkedCase);
    }


    @Test
    public void shouldCreateDefaultHearings() {
        final int count = 3;
        final List<LinkedCaseDocument> hearings = linkedCaseDocumentMother.defaultLinkedCases(count);
        for (final LinkedCaseDocument linkedCaseDocument : hearings) {
            assertLinkedCase(linkedCaseDocument);
        }
    }

    @Test
    public void shouldCreateDefaultHearingsAsBuilder() {
        final int count = 7;
        final List<LinkedCaseDocument.Builder> builders = linkedCaseDocumentMother.defaultLinkedCasesAsBuilder(count);
        assertThat(builders, hasSize(count));
    }

    @Test
    public void shouldCreateDefaultHearingAsBuilder() {
        final LinkedCaseDocument.Builder builder = linkedCaseDocumentMother.defaultLinkedCaseAsBuilder();
        assertThat(builder, is(notNullValue()));
    }


    private void assertLinkedCase(final LinkedCaseDocument linkedCase) {
        assertThat(linkedCase.getLinkedCaseId(), is(notNullValue()));
    }
}
