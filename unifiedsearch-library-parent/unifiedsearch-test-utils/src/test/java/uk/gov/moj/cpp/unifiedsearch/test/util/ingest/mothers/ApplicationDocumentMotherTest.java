package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.ApplicationExternalCreatorType.PROSECUTOR;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother.defaultApplication;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother.defaultApplicationAsBuilder;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother.defaultApplications;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother.defaultApplicationsAsBuilder;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.ApplicationDocument;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ApplicationDocumentMotherTest {

    @Test
    public void shouldCreateDefaultApplication() {
        final ApplicationDocument application = defaultApplication();
        assertThat(application.getApplicationId(), is(notNullValue()));
    }

    @Test
    public void shouldCreateDefaultApplications() {
        final int count = 2;
        final List<ApplicationDocument> applications = defaultApplications(count);
        assertThat(applications, hasSize(count));
        for (final ApplicationDocument application : applications) {
            assertThat(application.getApplicationId(), is(notNullValue()));
            assertThat(application.getApplicationStatus(), is(notNullValue()));
            assertThat(application.getApplicationExternalCreatorType(), is(PROSECUTOR.name()));
        }
    }

    @Test
    public void shouldCreateDefaultApplicationsAsBuilder() {
        final int appCount = 3;
        final List<ApplicationDocument.Builder> applicationBuilders = defaultApplicationsAsBuilder(appCount);
        assertThat(applicationBuilders, hasSize(appCount));
        for (final ApplicationDocument.Builder builder : applicationBuilders) {
            assertThat(builder, is(notNullValue()));
        }
    }

    @Test
    public void shouldCreateDefaultApplicationAsBuilder() {
        final ApplicationDocument.Builder builder = defaultApplicationAsBuilder();
        assertThat(builder, is(notNullValue()));
    }
}