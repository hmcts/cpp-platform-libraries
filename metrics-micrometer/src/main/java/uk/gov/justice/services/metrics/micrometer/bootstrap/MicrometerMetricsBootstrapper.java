package uk.gov.justice.services.metrics.micrometer.bootstrap;

import uk.gov.justice.services.eventstore.metrics.tags.TagProvider;
import uk.gov.justice.services.metrics.micrometer.registry.MetricsRegistrar;

import javax.inject.Inject;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.slf4j.Logger;

public class MicrometerMetricsBootstrapper {

    @Inject
    private CompositeMeterRegistry compositeMeterRegistry;

    @Inject
    private MetricsRegistrar metricsRegistrar;

    @Inject
    private AllMeterRegistrar allMeterRegistrar;

    @Inject
    private Logger logger;

    @Inject
    private TagProvider tagProvider;

    public void bootstrapMetrics() {

        logger.info("Starting micrometer metrics");

        compositeMeterRegistry.config().commonTags(tagProvider.getCommonTags());

        allMeterRegistrar.registerAllMetersFor(compositeMeterRegistry);
        metricsRegistrar.addRegistry(compositeMeterRegistry);
    }
}
