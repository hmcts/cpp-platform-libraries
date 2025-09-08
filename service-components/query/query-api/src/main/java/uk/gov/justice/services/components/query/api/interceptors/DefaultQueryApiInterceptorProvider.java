package uk.gov.justice.services.components.query.api.interceptors;

import uk.gov.justice.services.core.audit.LocalAuditInterceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;

public class DefaultQueryApiInterceptorProvider implements AuditInterceptorProvider {

    public InterceptorChainEntry createAuditInterceptorEntry(int priority) {
        return new InterceptorChainEntry(priority, LocalAuditInterceptor.class);
    }
}
