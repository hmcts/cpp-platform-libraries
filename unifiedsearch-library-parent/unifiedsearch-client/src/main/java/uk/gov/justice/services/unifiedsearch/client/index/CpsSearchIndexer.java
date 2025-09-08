package uk.gov.justice.services.unifiedsearch.client.index;

import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CpsSearchIndexer extends AbstractSearchIndexer implements UnifiedSearchIndexer {

    @Override
    public String getIndexSchemaFile() {
        return IndexInfo.CPS_CASE.getIndexSchemaFile();
    }

    @Override
    public String getIndexName() {
        return IndexInfo.CPS_CASE.getIndexName();
    }
}
