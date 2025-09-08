package uk.gov.justice.services.components.query.api.interceptors;

import uk.gov.justice.services.core.interceptor.InterceptorChainEntry;

public interface AuditInterceptorProvider {

    InterceptorChainEntry createAuditInterceptorEntry(int priority);
}
