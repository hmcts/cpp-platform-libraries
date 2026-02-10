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

import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CaseDocument;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.ApplicationDocumentMother;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.HearingDocumentMother;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.PartyDocumentMother;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        when(elasticSearchClient.restClient(CRIME_CASE).bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);

        elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument));
        assertThat(bulkResponse.errors(), is(false));
    }

    @Test
    public void shouldFailIfResponseHasFailures() throws IOException {
        final String exceptionMessage = "Some really bad exception";

        final CaseDocument caseDocument = getCrimeCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CRIME_CASE).bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(true);
        when(bulkResponse.items()).thenReturn(Collections.singletonList(BulkResponseItem.of(t -> t.error(ErrorCause.of( e -> e.reason(exceptionMessage)))
                .operationType(OperationType.Create).index("index").status(1))));

        var e = assertThrows(RuntimeException.class, () -> elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument)));
        assertThat(e.getMessage(), is("BulkRequest failed: ErrorCause: {\"reason\":\"Some really bad exception\"}"));
    }

    @Test
    public void shouldIngestCpsCaseData() throws IOException {
        final uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument caseDocument = getCpsCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CPS_CASE).bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(false);

        elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument));
        assertThat(bulkResponse.errors(), is(false));
    }

    @Test
    public void shouldFailIfResponseHasFailuresForCpsCase() throws IOException {

        final String exceptionMessage = "Some really bad exception";

        final uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument caseDocument = getCpsCaseDocument();
        final BulkResponse bulkResponse = mock(BulkResponse.class);

        when(elasticSearchClient.restClient(CPS_CASE).bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        when(bulkResponse.errors()).thenReturn(true);
        when(bulkResponse.items()).thenReturn(Collections.singletonList(BulkResponseItem.of(t -> t.error(ErrorCause.of( e -> e.reason(exceptionMessage)))
                .operationType(OperationType.Create).index("index").status(1))));

        var e = assertThrows(RuntimeException.class, () -> elasticSearchIndexIngestorUtil.ingestCaseData(asList(caseDocument)));
        assertThat(e.getMessage(), is("BulkRequest failed: ErrorCause: {\"reason\":\"Some really bad exception\"}"));
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