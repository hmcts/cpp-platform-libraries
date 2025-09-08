package uk.gov.justice.services.unifiedsearch.client.restclient;

import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.CPS_WRITE_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.MONITOR_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.READ_USER;
import static uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchSecurityConstants.WRITE_USER;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.elasticsearch.client.RestHighLevelClient;

@ApplicationScoped
public class UnifiedSearchHighLevelRestClientProducer {

    @Inject
    private HighLevelRestClientProvider highLevelRestClientProvider;

    @Produces
    @Named(WRITE_USER)
    public RestHighLevelClient getWriteHighLevelClient() {
        return highLevelRestClientProvider.newHighLevelClientFor(WRITE_USER);
    }

    @Produces
    @Named(READ_USER)
    public RestHighLevelClient getReadHighLevelClient() {
        return highLevelRestClientProvider.newHighLevelClientFor(READ_USER);
    }

    @Produces
    @Named(MONITOR_USER)
    public RestHighLevelClient getMonitorHighLevelClient() {
        return highLevelRestClientProvider.newHighLevelClientFor(MONITOR_USER);
    }

    @Produces
    @Named(CPS_WRITE_USER)
    public RestHighLevelClient getCpsWriteHighLevelClient() {
        return highLevelRestClientProvider.newHighLevelClientFor(CPS_WRITE_USER);
    }

    @Produces
    @Named(CPS_READ_USER)
    public RestHighLevelClient getCpsReadHighLevelClient() {
        return highLevelRestClientProvider.newHighLevelClientFor(CPS_READ_USER);
    }
}
