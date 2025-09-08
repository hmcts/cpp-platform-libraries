package uk.gov.justice.services.unifiedsearch.client.index;

import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_WRITE_USER;

import uk.gov.justice.services.unifiedsearch.client.domain.cps.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.transformer.cps.CpsCaseDetailsTransformer;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;

@ApplicationScoped
@IndexType(IndexConstants.CPS_CASE_INDEX_NAME)
public class CpsCaseDocumentService extends DocumentService {

    @Inject
    @Named(CPS_WRITE_USER)
    private RestHighLevelClient restHighLevelClient;

    @Inject
    private CpsCaseDetailsTransformer caseDetailsTransformer;

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
