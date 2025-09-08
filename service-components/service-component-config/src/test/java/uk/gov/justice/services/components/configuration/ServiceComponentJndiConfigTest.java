package uk.gov.justice.services.components.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceComponentJndiConfigTest {

    @InjectMocks
    private ServiceComponentJndiConfig serviceComponentJndiConfig;

    @Test
    public void shouldGetProvideTheValueOfCommandApiAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isCommandApiAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "commandApiAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isCommandApiAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfCommandControllerAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isCommandControllerAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "commandControllerAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isCommandControllerAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfCommandHandlerAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isCommandHandlerAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "commandHandlerAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isCommandHandlerAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfEventApiAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isEventApiAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "eventApiAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isEventApiAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfQueryApiAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isQueryApiAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "queryApiAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isQueryApiAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfQueryControllerAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isQueryControllerAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "queryControllerAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isQueryControllerAuditEnabled(), is(true));
    }

    @Test
    public void shouldGetProvideTheValueOfQueryViewAuditEnabled() throws Exception {

        assertThat(serviceComponentJndiConfig.isQueryViewAuditEnabled(), is(false));

        setField(serviceComponentJndiConfig, "queryViewAuditEnabled", "true");

        assertThat(serviceComponentJndiConfig.isQueryViewAuditEnabled(), is(true));
    }
}
