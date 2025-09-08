package uk.gov.justice.services.unifiedsearch.client.index;

import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsTransformer;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;

@ApplicationScoped
@IndexType(IndexConstants.CRIME_CASE_INDEX_NAME)
public class CrimeCaseDocumentService extends DocumentService {

    @Inject
    @Named(WRITE_USER)
    private RestHighLevelClient restHighLevelClient;

    @Inject
    private CaseDetailsTransformer caseDetailsTransformer;

    @Override
    protected RestHighLevelClient restHighLevelClient() {
        return restHighLevelClient;
    }

    @Override
    protected Object getTransformedCaseDetails(JsonObject document,
                                               GetResponse getResponse) throws IOException {
        return getResponse.isExists() ?
                caseDetailsTransformer.transform(document, getResponse) :
                objectMapper.readValue(document.toString(), CaseDetails.class);
    }
}
