package uk.gov.justice.services.unifiedsearch.client.retry;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

import uk.gov.justice.services.common.configuration.Value;
import uk.gov.justice.services.core.interceptor.Interceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIngestionException;

import javax.inject.Inject;
import javax.json.JsonObject;

import org.slf4j.Logger;

/**
 * Interceptor designed to catch the {@link UnifiedSearchIngestionRetryFailedException} to retry
 * processing of an envelope within the same transaction to avoid JMS rollback.
 */
public class UnifiedSearchIngestionRetryInterceptor implements Interceptor {

    @Inject
    private Logger logger;

    /**
     * Amount of retries the handler will attempt before failing.
     */
    @Inject
    @Value(key = "index.retry.wait.millis", defaultValue = "1000")
    String waitTime;

    @Inject
    @Value(key = "index.retry.max.retries", defaultValue = "3")
    String maxRetry;


    @Override
    public InterceptorContext process(final InterceptorContext interceptorContext, final InterceptorChain interceptorChain) {
        final int maxRetryCount = parseInt(maxRetry);
        final int retryWaitTime = parseInt(waitTime);
        final JsonObject metadata = interceptorContext.inputEnvelope().metadata().asJsonObject();

        int retries = maxRetryCount;
        UnifiedSearchIngestionException exception = null;

        while (retries > 0) {
            try {
                retries--;

                final int retryCount = maxRetryCount - retries;
                if (retryCount > 1) {
                    logger.debug(format("Unified search ingestion retry attempt %d", retryCount));
                }
                return interceptorChain.processNext(interceptorContext);
            } catch (final UnifiedSearchIngestionException unifiedSearchIngestionException) {
                logger.debug(format("Unified search ingestion has failed on event  %s at retry attempt %d", metadata, maxRetryCount - retries));
                waitFor(retryWaitTime);
                exception = unifiedSearchIngestionException;
            }
        }
        throw new UnifiedSearchIngestionRetryFailedException(format("Retry count of %d exceeded for ingestion %s", maxRetryCount, metadata), exception);
    }

    private void waitFor(final int retryWaitTime) {
        try {
            sleep(retryWaitTime);
        } catch (final InterruptedException ex) {
            currentThread().interrupt();
        }
    }
}
