package uk.gov.justice.services.unifiedsearch.client.index;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DocumentSelector {

    @Inject
    @Any
    private Instance<DocumentService> documentServiceInstance;

    public DocumentService getDocumentServiceByIndex(final String index) {
        final IndexTypeLiteral qualifier = new IndexTypeLiteral(index);
        return documentServiceInstance.select(qualifier).get();
    }

}
