package uk.gov.justice.services.components.event.listener.interceptors;

import static java.lang.String.format;

import uk.gov.justice.services.core.interceptor.Interceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.logging.DebugLogger;

import javax.inject.Inject;

import org.slf4j.Logger;

public class EventListenerDebugLoggingInterceptor implements Interceptor {

    @Inject
    private DebugLogger debugLogger;

    @Inject
    private Logger logger;

    @Override
    public InterceptorContext process(final InterceptorContext interceptorContext, final InterceptorChain interceptorChain) {

        debugLogger.debug(logger, () -> format("Processing Event Name : %s", interceptorContext.inputEnvelope().metadata().name()));

        return interceptorChain.processNext(interceptorContext);
    }
}
