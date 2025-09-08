package uk.gov.moj.cpp.accesscontrol.drools;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.accesscontrol.drools.listener.TrackingAgendaEventListener;
import uk.gov.moj.cpp.accesscontrol.drools.listener.TrackingRuleRuntimeEventListener;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;
import uk.gov.moj.cpp.accesscontrol.providers.ProviderFoundEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KieSessionFactoryTest {

    @Mock
    KieContainer kieContainer;

    @Mock
    KieBaseModel kieBaseModel1;

    @Mock
    KieBaseModel kieBaseModel2;

    @Mock
    KieSessionModel kieSessionModel1;

    @Mock
    KieSessionModel kieSessionModel2;

    @Mock
    KieSessionConfiguration kieSessionConfiguration1;

    @Mock
    KieSessionConfiguration kieSessionConfiguration2;

    @Mock
    private StatelessKieSession ksession1;

    @Mock
    private StatelessKieSession ksession2;

    @Mock
    private BeanManager beanManager;

    @Mock
    private Bean<Object> bean;

    private TestProvider testProvider;

    @Mock
    private Context context;

    @Mock
    private CreationalContext<Object> creationalContext;

    @Mock
    private KieContainerProvider kieContainerProvider;

    @InjectMocks
    private KieSessionFactory kieSessionsFactory;

    @BeforeEach
    public void setup() {
        testProvider = new TestProvider();
    }

    @Test
    public void shouldLoadProviderFromEvents() throws Exception {
        final String kieBaseName1 = "COMMAND_API";
        final String kieBaseName2 = "QUERY_API";

        final List<String> baseNames = new ArrayList<>();
        baseNames.add(kieBaseName1);
        baseNames.add(kieBaseName2);

        doReturn(TestProvider.class).when(bean).getBeanClass();
        when(beanManager.getContext(bean.getScope())).thenReturn(context);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
        when(context.get(bean, creationalContext)).thenReturn(testProvider);
        when(kieContainerProvider.getKieClasspathContainer()).thenReturn(kieContainer);

        setKieExpectations(baseNames);
        kieSessionsFactory.register(new ProviderFoundEvent(bean));

        verify(ksession1, times(1)).setGlobal(Mockito.eq("testProvider"), Mockito.eq(testProvider));
        verify(ksession1, times(1)).addEventListener(Mockito.any(TrackingAgendaEventListener.class));
        verify(ksession1, times(1)).addEventListener(Mockito.any(TrackingRuleRuntimeEventListener.class));
        verify(ksession2, times(1)).setGlobal(Mockito.eq("testProvider"), Mockito.eq(testProvider));
        verify(ksession2, times(1)).addEventListener(Mockito.any(TrackingAgendaEventListener.class));
        verify(ksession2, times(1)).addEventListener(Mockito.any(TrackingRuleRuntimeEventListener.class));
    }

    @Test
    public void shouldThrowExceptionLoadProviderFromEvents() throws Exception {
        final String kieBaseName1 = "COMMAND_API";
        final String kieBaseName2 = "QUERY_API";

        final List<String> baseNames = new ArrayList<>();
        baseNames.add(kieBaseName1);
        baseNames.add(kieBaseName2);

        setKieExpectations(baseNames);
        when(kieContainerProvider.getKieClasspathContainer()).thenReturn(kieContainer);

        assertThrows(IllegalArgumentException.class, () -> kieSessionsFactory.getKieSession("COMMAND_HANDLER"));
    }

    private void setKieExpectations(final List<String> baseNames) {


        final String kieSessionNames1 = "kieSession1";
        final String kieSessionNames2 = "kieSession2";

        final Map<String, KieSessionModel> sessionMap1 = new HashMap<>();
        sessionMap1.put(kieSessionNames1, kieSessionModel1);

        final Map<String, KieSessionModel> sessionMap2 = new HashMap<>();
        sessionMap2.put(kieSessionNames2, kieSessionModel2);

        when(kieBaseModel1.getKieSessionModels()).thenReturn(sessionMap1);
        when(kieBaseModel2.getKieSessionModels()).thenReturn(sessionMap2);
        when(kieContainer.getKieBaseNames()).thenReturn(baseNames);
        when(kieContainer.getKieBaseModel(baseNames.get(0))).thenReturn(kieBaseModel1);
        when(kieContainer.getKieBaseModel(baseNames.get(1))).thenReturn(kieBaseModel2);
        when(kieContainer.getKieSessionConfiguration(kieSessionNames1)).thenReturn(kieSessionConfiguration1);
        when(kieContainer.getKieSessionConfiguration(kieSessionNames2)).thenReturn(kieSessionConfiguration2);
        when(kieSessionModel1.getName()).thenReturn(kieSessionNames1);
        when(kieSessionModel2.getName()).thenReturn(kieSessionNames2);
        when(kieContainer.newStatelessKieSession(kieSessionModel1.getName(), kieSessionConfiguration1)).thenReturn(ksession1);
        when(kieContainer.newStatelessKieSession(kieSessionModel2.getName(), kieSessionConfiguration2)).thenReturn(ksession2);
    }

    @Provider
    public class TestProvider {

    }
}
