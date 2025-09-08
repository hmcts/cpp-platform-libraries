package uk.gov.moj.cpp.accesscontrol.providers;

import javax.enterprise.inject.spi.Bean;

/**
 * Event representing the occurrence of a class with an annotation having been
 * identified by the framework.
 */
public class ProviderFoundEvent {

    private final Bean<?> bean;

    public ProviderFoundEvent(final Bean<?> bean) {
        this.bean = bean;
    }

    public Bean<?> getBean() {
        return bean;
    }

}
