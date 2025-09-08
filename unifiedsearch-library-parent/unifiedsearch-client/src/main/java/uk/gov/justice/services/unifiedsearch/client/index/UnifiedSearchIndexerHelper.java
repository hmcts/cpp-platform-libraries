package uk.gov.justice.services.unifiedsearch.client.index;

import static java.util.UUID.fromString;
import static uk.gov.justice.services.messaging.JsonObjects.getString;

import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;

@ApplicationScoped
public class UnifiedSearchIndexerHelper {

    public static final String CASE_ID = "caseId";

    public UUID getCaseId(final JsonObject index) {
        final String caseId = getString(index, CASE_ID).orElseThrow(() -> new UnifiedSearchClientException("Case id not present for index"));
        return fromString(caseId);
    }
}
