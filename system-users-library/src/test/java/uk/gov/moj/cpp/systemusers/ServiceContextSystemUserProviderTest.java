package uk.gov.moj.cpp.systemusers;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.configuration.ServiceContextNameProvider;

import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceContextSystemUserProviderTest {

    @Mock
    private ServiceContextNameProvider serviceContextNameProvider;

    @Mock
    private InitialContext initialContext;

    @InjectMocks
    private ServiceContextSystemUserProvider serviceContextSystemUserProvider;

    @Test
    public void shouldReturnEmptyOptional_whenGettingSystemUserId_forInvalidContext() throws Exception {
        setContext("unknown-context");
        assertThat(serviceContextSystemUserProvider.getContextSystemUserId(), is(empty()));
    }

    @Test
    public void shouldGetSystemUserId_forValidContext() throws Exception {
        setContext("context-command-api");
        final UUID jndiSystemUserId = mockJNDIDirectoryEntry("context");
        assertThat(serviceContextSystemUserProvider.getContextSystemUserId(), is(of(jndiSystemUserId)));
    }

    private void setContext(String contextName) {
        when(serviceContextNameProvider.getServiceContextName()).thenReturn(contextName);
        serviceContextSystemUserProvider.setInitialContext(initialContext);
        serviceContextSystemUserProvider.setup();
    }

    private UUID mockJNDIDirectoryEntry(final String contextName) throws NamingException {
        final UUID jndiSystemUserId = UUID.randomUUID();
        when(initialContext.lookup("java:/app/" + contextName + "/users/system-user-id")).thenReturn(jndiSystemUserId.toString());
        serviceContextSystemUserProvider.setup();
        return jndiSystemUserId;
    }

}
