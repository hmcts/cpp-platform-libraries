package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.CaseType.PROSECUTION;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CaseDocumentMother.defaultCase;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CaseDocumentMother.defaultCaseAsBuilder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CaseDocumentMother.defaultCases;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.CaseDocumentMother.defaultCasesAsBuilderList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CaseDocument;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CaseDocumentMotherTest {

    @Test
    public void shouldCreateDefaultCase() {
        final CaseDocument caseDocument = defaultCase();
        assertCase(caseDocument);
    }


    @Test
    public void shouldCreateDefaultCases() {
        final int count = 2;
        final List<CaseDocument> cases = defaultCases(count);
        assertThat(cases, hasSize(count));
        for (final CaseDocument caseDocument : cases) {
            assertCase(caseDocument);
        }
    }

    @Test
    public void shouldCreateDefaultCasesAsBuilderList() {
        final int count = 7;
        final List<CaseDocument.Builder> builders = defaultCasesAsBuilderList(count);
        assertThat(builders, hasSize(count));
    }

    @Test
    public void shouldCreateDefaultCaseAsBuilder() {

        final CaseDocument.Builder builder = defaultCaseAsBuilder();

        assertThat(builder.getApplicationBuilders().size(), is(1));
        assertThat(builder.getHearingBuilders().size(), is(1));

        assertCase(builder.build());
    }

    private void assertCase(final CaseDocument caseDocument) {
        assertThat(caseDocument.getCaseId(), is(notNullValue()));
        assertThat(caseDocument.getCaseReference(), is(notNullValue()));
        assertThat(caseDocument.getCaseStatus(), is("ACTIVE"));
        assertThat(caseDocument.get_case_type(), is(PROSECUTION.name()));
    }
}