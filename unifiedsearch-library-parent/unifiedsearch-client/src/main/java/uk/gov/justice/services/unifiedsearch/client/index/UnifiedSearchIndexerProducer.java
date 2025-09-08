package uk.gov.justice.services.unifiedsearch.client.index;

import static java.lang.String.format;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CPS_CASE;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo.CRIME_CASE;

import uk.gov.justice.services.cdi.QualifierAnnotationExtractor;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchIndexer;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchName;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

@ApplicationScoped
public class UnifiedSearchIndexerProducer {

    @Inject
    private QualifierAnnotationExtractor qualifierAnnotationExtractor;

    @Inject
    private DefaultUnifiedSearchIndexer defaultUnifiedSearchIndexer;

    @Inject
    private CpsSearchIndexer cpsSearchIndexer;

    @Produces
    @UnifiedSearchName
    public UnifiedSearchIndexer unifiedSearchClient(final InjectionPoint injectionPoint) {

        final UnifiedSearchName unifiedSearchName = qualifierAnnotationExtractor.getFrom(injectionPoint, UnifiedSearchName.class);

        if (unifiedSearchName.value().equals(CRIME_CASE.getIndexName())){
            return defaultUnifiedSearchIndexer;
        }

        if (unifiedSearchName.value().equals(CPS_CASE.getIndexName())){
            return cpsSearchIndexer;
        }
        
        throw new UnifiedSearchClientException(format("Unknown @UnifiedSearchName value: %s", unifiedSearchName.value()));
    }
}
