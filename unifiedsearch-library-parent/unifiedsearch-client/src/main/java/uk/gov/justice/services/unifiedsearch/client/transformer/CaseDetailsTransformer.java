package uk.gov.justice.services.unifiedsearch.client.transformer;

import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;

@ApplicationScoped
public class CaseDetailsTransformer {

    @Inject
    private CaseDetailsNestedTransformer caseDetailsNestedTransformer;

    @Inject
    private ObjectMapper mapper;

    public CaseDetails transform(final JsonObject index, final GetResponse getResponse) {
        try {
            final CaseDetails incomingIndexData = mapper.readValue(index.toString(), CaseDetails.class);
            final CaseDetails unifiedSearchIndexData = mapper.readValue(getResponse.getSourceAsString(), CaseDetails.class);

            if (incomingIndexData.getCaseReference() != null) {
                unifiedSearchIndexData.setCaseReference(incomingIndexData.getCaseReference());
            }
            if (incomingIndexData.getProsecutingAuthority() != null) {
                unifiedSearchIndexData.setProsecutingAuthority(incomingIndexData.getProsecutingAuthority());
            }
            if (incomingIndexData.getCaseStatus() != null) {
                unifiedSearchIndexData.setCaseStatus(incomingIndexData.getCaseStatus());
            }
            if (incomingIndexData.get_case_type() != null) {
                unifiedSearchIndexData.set_case_type(incomingIndexData.get_case_type());
            }
            if (incomingIndexData.get_is_sjp() != null) {
                unifiedSearchIndexData.set_is_sjp(incomingIndexData.get_is_sjp());
            }
            if (incomingIndexData.get_is_magistrates() != null) {
                unifiedSearchIndexData.set_is_magistrates(incomingIndexData.get_is_magistrates());
            }
            if (incomingIndexData.get_is_crown() != null) {
                unifiedSearchIndexData.set_is_crown(incomingIndexData.get_is_crown());
            }
            if (incomingIndexData.get_is_charging() != null) {
                unifiedSearchIndexData.set_is_charging(incomingIndexData.get_is_charging());
            }
            if (incomingIndexData.getSjpNoticeServed() != null) {
                unifiedSearchIndexData.setSjpNoticeServed(incomingIndexData.getSjpNoticeServed());
            }
            caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);
            caseDetailsNestedTransformer.mergeHearings(incomingIndexData, unifiedSearchIndexData);
            caseDetailsNestedTransformer.mergeApplications(incomingIndexData, unifiedSearchIndexData);

            if (incomingIndexData.getSourceSystemReference() != null) {
                unifiedSearchIndexData.setSourceSystemReference(incomingIndexData.getSourceSystemReference());
            }
            return unifiedSearchIndexData;
        } catch (final IOException ioex) {
            throw new UnifiedSearchIndexTransformerException("Unable to transform index ", ioex);
        }
    }


}
