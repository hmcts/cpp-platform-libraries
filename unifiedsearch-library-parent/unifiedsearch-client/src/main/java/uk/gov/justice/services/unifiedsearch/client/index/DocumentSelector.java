package uk.gov.justice.services.unifiedsearch.client.index;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

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
