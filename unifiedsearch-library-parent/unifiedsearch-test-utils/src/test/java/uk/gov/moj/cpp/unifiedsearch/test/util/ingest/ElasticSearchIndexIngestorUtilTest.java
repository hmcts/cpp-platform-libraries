package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CPS_CASE;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.IndexInfo.CRIME_CASE;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CaseDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchIndexIngestorUtilTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ElasticSearchClient elasticSearchClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ElasticSearchIndexIngestorUtil elasticSearchIndexIngestorUtil;


    @Test
    public void shouldIngestData() throws IOException {
        final CaseDocument caseDocument = getCrimeCaseDocument();

        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CRIME_CASE).bulk(any(BulkRequest.class), any(RequestOptions.class))).thenReturn(bulkResponse);
        when(bulkResponse.hasFailures()).thenReturn(false);
        when(objectMapper.writeValueAsString(caseDocument)).thenReturn("test");

        elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument));
        assertThat(bulkResponse.hasFailures(), is(false));
    }

    @Test
    public void shouldFailIfResponseHasFailures() throws IOException {
        final String exceptionMessage = "Some really bad exception";

        final CaseDocument caseDocument = getCrimeCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CRIME_CASE).bulk(any(BulkRequest.class), any(RequestOptions.class))).thenReturn(bulkResponse);
        when(bulkResponse.hasFailures()).thenReturn(true);
        when(bulkResponse.buildFailureMessage()).thenReturn(exceptionMessage);
        when(objectMapper.writeValueAsString(caseDocument)).thenReturn("test");

        var e = assertThrows(RuntimeException.class, () -> elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument)));
        assertThat(e.getMessage(), is("BulkRequest failed: " + exceptionMessage));
    }

    @Test
    public void shouldIngestCpsCaseData() throws IOException {
        final uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument caseDocument = getCpsCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CPS_CASE).bulk(any(BulkRequest.class), any(RequestOptions.class))).thenReturn(bulkResponse);
        when(bulkResponse.hasFailures()).thenReturn(false);
        when(objectMapper.writeValueAsString(caseDocument)).thenReturn("test");

        elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument));
        assertThat(bulkResponse.hasFailures(), is(false));
    }

    @Test
    public void shouldFailIfResponseHasFailuresForCpsCase() throws IOException {

        final String exceptionMessage = "Some really bad exception";

        final uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument caseDocument = getCpsCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CPS_CASE).bulk(any(BulkRequest.class), any(RequestOptions.class))).thenReturn(bulkResponse);
        when(bulkResponse.hasFailures()).thenReturn(true);
        when(bulkResponse.buildFailureMessage()).thenReturn(exceptionMessage);
        when(objectMapper.writeValueAsString(caseDocument)).thenReturn("test");

        var e = assertThrows(RuntimeException.class, () -> elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument)));
        assertThat(e.getMessage(), is("BulkRequest failed: " + exceptionMessage));
    }

    private CaseDocument getCrimeCaseDocument() {
        return new CaseDocument.Builder()
                .withCaseId(UUID.randomUUID().toString())
                .withParties(PartyDocumentMother.defaultPartyBuildersList())
                .withHearings(HearingDocumentMother.defaultHearingsAsBuilder(1))
                .withApplications(ApplicationDocumentMother.defaultApplicationsAsBuilder(1))
                .build();
    }

    private uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument getCpsCaseDocument() {
        return new uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument.Builder()
                .withCaseId(UUID.randomUUID().toString())
                .withParties(uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps.PartyDocumentMother.defaultPartyList())
                .withHearings(uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps.HearingDocumentMother.defaultHearings(1))
                .build();
    }
}