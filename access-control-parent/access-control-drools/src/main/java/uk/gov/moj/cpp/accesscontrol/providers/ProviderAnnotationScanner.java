package uk.gov.moj.cpp.accesscontrol.providers;

import static java.util.Collections.synchronizedList;
import static org.slf4j.LoggerFactory.getLogger;

import uk.gov.justice.services.core.annotation.AnyLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;

/**
 * Scans all beans and processes framework specific annotations.
 */
public class ProviderAnnotationScanner implements Extension {

    private static final Logger LOGGER = getLogger(ProviderAnnotationScanner.class);

    private List<Object> events = synchronizedList(new ArrayList<>());

    @SuppressWarnings("unused")
    void afterDeploymentValidation(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) {
        allBeansFrom(beanManager)
                .filter(this::isProvider)
                .forEach(this::processBean);

        fireAllCollectedEvents(beanManager);
    }

    private void processBean(final Bean<?> bean) {
        events.add(new ProviderFoundEvent(bean));
        LOGGER.info("Identified Access Control Provider {}", bean.getBeanClass().getSimpleName());
    }

    private void fireAllCollectedEvents(final BeanManager beanManager) {
        events.forEach(beanManager::fireEvent);
    }

    private boolean isProvider(final Bean<?> bean) {
        return bean.getBeanClass().isAnnotationPresent(Provider.class);
    }

    private Stream<Bean<?>> allBeansFrom(final BeanManager beanManager) {
        return beanManager.getBeans(Object.class, AnyLiteral.create()).stream();
    }
}
