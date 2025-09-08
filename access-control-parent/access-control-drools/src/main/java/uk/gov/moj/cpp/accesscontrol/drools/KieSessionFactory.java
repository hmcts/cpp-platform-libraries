package uk.gov.moj.cpp.accesscontrol.drools;

import static uk.gov.moj.cpp.accesscontrol.drools.util.ProviderNameUtil.getProviderName;

import uk.gov.moj.cpp.accesscontrol.drools.listener.TrackingAgendaEventListener;
import uk.gov.moj.cpp.accesscontrol.drools.listener.TrackingRuleRuntimeEventListener;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;
import uk.gov.moj.cpp.accesscontrol.providers.ProviderFoundEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

@ApplicationScoped
public class KieSessionFactory {

    private final Map<String, StatelessKieSession> sessions = new HashMap<>();

    @Inject
    BeanManager beanManager;

    @Inject
    KieContainerProvider kieContainerProvider;

    /**
     * Registers all {@link Provider}'s to be injected into the kie session.
     *
     * @param event - Provider found events detected by the framework.
     * @throws IllegalAccessException if the provider fails to be loaded.
     * @throws InstantiationException if the provider fails to be loaded.
     */
    void register(@Observes final ProviderFoundEvent event) throws IllegalAccessException, InstantiationException, IllegalArgumentException {
        final Bean bean = event.getBean();
        final Object provider = beanManager.getContext(bean.getScope())
                .get(bean, beanManager.createCreationalContext(bean));
        for (final StatelessKieSession session : getKieSessions().values()) {
            session.setGlobal(getProviderName(event.getBean().getBeanClass().getSimpleName()), provider);
        }
    }

    public StatelessKieSession getKieSession(final String component) {
        StatelessKieSession session = getKieSessions().get(component);
        if (null == session) {
            throw new IllegalArgumentException("No KBase found for the component " + component);
        }
        return session;
    }

    private synchronized Map<String, StatelessKieSession> getKieSessions() {
        if (sessions.isEmpty()) {

            final KieContainer kieContainer = kieContainerProvider.getKieClasspathContainer();
            final Collection<String> kieBaseNames = kieContainer.getKieBaseNames();

            for (String kBaseName : kieBaseNames) {
                final KieBaseModel kieBaseModel = kieContainer.getKieBaseModel(kBaseName);
                final Map<String, KieSessionModel> sessionMap = kieBaseModel.getKieSessionModels();

                for (KieSessionModel sessionModel : sessionMap.values()) {
                    final KieSessionConfiguration sessionConfiguration = kieContainer.getKieSessionConfiguration(sessionModel.getName());
                    final StatelessKieSession kieSession = kieContainer.newStatelessKieSession(sessionModel.getName(), sessionConfiguration);
                    kieSession.addEventListener(new TrackingAgendaEventListener());
                    kieSession.addEventListener(new TrackingRuleRuntimeEventListener());
                    sessions.put(kBaseName, kieSession);
                }
            }
        }
        return sessions;
    }
}
