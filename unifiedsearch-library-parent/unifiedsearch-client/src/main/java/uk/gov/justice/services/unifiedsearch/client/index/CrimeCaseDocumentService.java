package uk.gov.justice.services.unifiedsearch.client.index;

import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsTransformer;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;

import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ApplicationScoped
@IndexType(IndexConstants.CRIME_CASE_INDEX_NAME)
public class CrimeCaseDocumentService extends DocumentService {

    @Inject
    @Named(WRITE_USER)
    private ElasticsearchClient elasticsearchClient;

    @Inject
    private CaseDetailsTransformer caseDetailsTransformer;

    @Override
    protected ElasticsearchClient elasticsearchClient() {
        return elasticsearchClient;
    }

    @Override
    protected Object getTransformedCaseDetails(JsonObject document,
                                               GetResponse getResponse) throws IOException {
        return getResponse.found() ?
                caseDetailsTransformer.transform(document, getResponse) :
                objectMapper.readValue(document.toString(), CaseDetails.class);
    }
}
