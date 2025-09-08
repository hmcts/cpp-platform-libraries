package uk.gov.justice.services.metrics.micrometer.bootstrap;

import uk.gov.justice.services.eventstore.metrics.meters.gauges.GaugeMeterFactory;
import uk.gov.justice.services.eventstore.metrics.tags.TagProvider;
import uk.gov.justice.services.metrics.micrometer.counters.CounterMeterFactory;

import javax.inject.Inject;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import uk.gov.justice.services.metrics.micrometer.meters.SourceComponentPair;

import java.util.List;

public class AllMeterRegistrar {

    @Inject
    private TagProvider tagProvider;

    @Inject
    private GaugeMeterFactory gaugeMeterFactory;

    @Inject
    private CounterMeterFactory counterMeterFactory;

    @Inject
    private GaugeMeterRegistrar gaugeMeterRegistrar;

    @Inject
    private CounterMeterRegistrar counterMeterRegistrar;

    public void registerAllMetersFor(final CompositeMeterRegistry compositeMeterRegistry) {


        final List<SourceComponentPair> sourceComponentPairs = tagProvider.getSourceComponentPairs();

        gaugeMeterFactory.createAllGaugeMetersForSourceAndComponents(sourceComponentPairs)
                .forEach(gaugeMetricsMeter ->
                        gaugeMeterRegistrar.registerGaugeMeter(gaugeMetricsMeter, compositeMeterRegistry));


        counterMeterFactory.createAllCounterMetersForSourceAndComponents(sourceComponentPairs)
                .forEach(counterMeter ->
                        counterMeterRegistrar.registerCounterMeter(counterMeter, compositeMeterRegistry));

    }

}
