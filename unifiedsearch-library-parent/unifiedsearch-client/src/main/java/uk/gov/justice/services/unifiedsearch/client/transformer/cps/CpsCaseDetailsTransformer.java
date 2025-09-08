package uk.gov.justice.services.unifiedsearch.client.transformer.cps;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import uk.gov.justice.services.unifiedsearch.client.domain.cps.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.transformer.UnifiedSearchIndexTransformerException;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;

@ApplicationScoped
public class CpsCaseDetailsTransformer {

    @Inject
    private CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer;

    @Inject
    private ObjectMapper mapper;

    public CaseDetails transform(final JsonObject index, final GetResponse getResponse) {
        try {
            final CaseDetails incomingIndexData = mapper.readValue(index.toString(), CaseDetails.class);
            final CaseDetails unifiedSearchIndexData = mapper.readValue(getResponse.getSourceAsString(), CaseDetails.class);

            if (incomingIndexData.getUrn() != null) {
                unifiedSearchIndexData.setUrn(incomingIndexData.getUrn());
            }
            if (incomingIndexData.getProsecutor() != null) {
                unifiedSearchIndexData.setProsecutor(incomingIndexData.getProsecutor());
            }
            if (incomingIndexData.getCaseStatusCode() != null) {
                unifiedSearchIndexData.setCaseStatusCode(incomingIndexData.getCaseStatusCode());
            }
            if (incomingIndexData.getCaseType() != null) {
                unifiedSearchIndexData.setCaseType(incomingIndexData.getCaseType());
            }
            if (isNotEmpty(incomingIndexData.getCjsAreaCodes())) {
                unifiedSearchIndexData.setCjsAreaCodes(incomingIndexData.getCjsAreaCodes());
            }
            if (incomingIndexData.getCpsAreaCode() != null) {
                unifiedSearchIndexData.setCpsAreaCode(incomingIndexData.getCpsAreaCode());
            }
            if (incomingIndexData.getCpsUnitCode() != null) {
                unifiedSearchIndexData.setCpsUnitCode(incomingIndexData.getCpsUnitCode());
            }
            if (incomingIndexData.getCrownAdvocate() != null) {
                unifiedSearchIndexData.setCrownAdvocate(incomingIndexData.getCrownAdvocate());
            }
            if (incomingIndexData.getOperationName() != null) {
                unifiedSearchIndexData.setOperationName(incomingIndexData.getOperationName());
            }
            if (incomingIndexData.getParalegalOfficer() != null) {
                unifiedSearchIndexData.setParalegalOfficer(incomingIndexData.getParalegalOfficer());
            }
            if (incomingIndexData.getWitnessCareUnitCode() != null) {
                unifiedSearchIndexData.setWitnessCareUnitCode(incomingIndexData.getWitnessCareUnitCode());
            }
            if (incomingIndexData.getWitnessCareOfficer() != null) {
                unifiedSearchIndexData.setWitnessCareOfficer(incomingIndexData.getWitnessCareOfficer());
            }
            if (incomingIndexData.getUnit() != null) {
                unifiedSearchIndexData.setUnit(incomingIndexData.getUnit());
            }
            if (incomingIndexData.getUnitGroup()!= null) {
                unifiedSearchIndexData.setUnitGroup(incomingIndexData.getUnitGroup());
            }
            cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);
            cpsCaseDetailsNestedTransformer.mergeHearings(incomingIndexData, unifiedSearchIndexData);
            cpsCaseDetailsNestedTransformer.mergeLinkedCases(incomingIndexData, unifiedSearchIndexData);

            return unifiedSearchIndexData;
        } catch (final IOException ioex) {
            throw new UnifiedSearchIndexTransformerException("Unable to transform index ", ioex);
        }
    }


}
