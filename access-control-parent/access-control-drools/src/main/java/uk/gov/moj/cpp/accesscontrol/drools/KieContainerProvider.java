package uk.gov.moj.cpp.accesscontrol.drools;

import jakarta.enterprise.context.ApplicationScoped;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

@ApplicationScoped
public class KieContainerProvider {

    public KieContainer getKieClasspathContainer() {
        return KieServices.get().getKieClasspathContainer();
    }
}
