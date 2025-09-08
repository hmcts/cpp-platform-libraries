package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CaseDocumentMotherTest {

    @InjectMocks
    private CaseDocumentMother caseDocumentMother;

    @Test
    public void shouldCreateDefaultCase() {
        final CaseDocument caseDocument = caseDocumentMother.defaultCase();
        assertCase(caseDocument);
    }


    @Test
    public void shouldCreateDefaultCases() {
        final int count = 2;
        final List<CaseDocument> cases = caseDocumentMother.defaultCases(count);
        assertThat(cases, hasSize(count));
        for (final CaseDocument caseDocument : cases) {
            assertCase(caseDocument);
        }
    }

    @Test
    public void shouldCreateDefaultCasesAsBuilderList() {
        final int count = 7;
        final List<CaseDocument.Builder> builders = caseDocumentMother.defaultCasesAsBuilderList(count);
        assertThat(builders, hasSize(count));
    }

    @Test
    public void shouldCreateDefaultCaseAsBuilder() {

        final CaseDocument.Builder builder = caseDocumentMother.defaultCaseAsBuilder();
        assertCase(builder.build());
    }

    @Test
    public void shouldCreateDefaultCaseAsBuilderWithCaseDetails() {

        final CaseDocument.Builder builder = caseDocumentMother.defaultCaseAsBuilder(randomUUID().toString(),randomUUID().toString());
        assertCase(builder.build());
    }

    private void assertCase(final CaseDocument caseDocument) {
        assertThat(caseDocument.getCaseId(), is(notNullValue()));
        assertThat(caseDocument.getUrn(), is(notNullValue()));
        assertThat(caseDocument.getCaseStatusCode(), is("ACTIVE"));
        assertThat(caseDocument.getCaseType(), is("CHARGED"));
        assertThat(caseDocument.getCpsAreaCode(), is("180"));
    }
}