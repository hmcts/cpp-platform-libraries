package uk.gov.moj.cpp.systemusers;

import static java.lang.String.format;

import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;
import uk.gov.moj.cpp.systemusers.exception.SystemUserProviderException;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "WeakerAccess"})
@ApplicationScoped
@Alternative
@Priority(2)
public class ServiceContextSystemUserProvider implements SystemUserProvider {

    @Inject
    private ServiceContextNameProvider serviceContextNameProvider;

    private Optional<UUID> currentContextSystemUserId;

    private InitialContext initialContext;

    @PostConstruct
    void setup() {
        try {
            if (initialContext == null) {
                initialContext = new InitialContext();
            }
        } catch (NamingException e) {
            throw new SystemUserProviderException("Error instantiating InitialContext.", e);
        }

        currentContextSystemUserId = getSystemUserIdForContext(getCurrentContext());
    }

    @Override
    public Optional<UUID> getContextSystemUserId() {

        return currentContextSystemUserId;
    }

    private String getCurrentContext() {
        final String contextFullName = serviceContextNameProvider.getServiceContextName();
        return contextFullName.substring(0, contextFullName.indexOf('-'));
    }

    private Optional<UUID> getSystemUserIdForContext(final String contextName) {
        Optional<String>  contextSystemUserId;

        try {
            contextSystemUserId = Optional.ofNullable((String) initialContext.lookup("java:/app/" + contextName + "/users/system-user-id"));
        } catch (NamingException e) {
            throw new SystemUserProviderException(format("Failed to look-up system user ID for context '%s'.", contextName), e);
        }

        return contextSystemUserId.map(UUID::fromString);
    }

    public void setInitialContext(final InitialContext initialContext) {
        this.initialContext = initialContext;
    }
}
