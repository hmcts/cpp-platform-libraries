package uk.gov.justice.services.metrics.micrometer.bootstrap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.eventstore.metrics.meters.gauges.EventStreamGaugeMeter;
import uk.gov.justice.services.eventstore.metrics.meters.gauges.GaugeMeterFactory;
import uk.gov.justice.services.eventstore.metrics.tags.TagProvider;
import uk.gov.justice.services.metrics.micrometer.meters.MetricsMeter;
import uk.gov.justice.services.metrics.micrometer.meters.SourceComponentPair;
import uk.gov.justice.services.metrics.micrometer.counters.CounterMeterFactory;

import java.util.List;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AllMeterRegistrarTest {

    @Mock
    private TagProvider tagProvider;

    @Mock
    private GaugeMeterFactory gaugeMeterFactory;

    @Mock
    private CounterMeterFactory counterMeterFactory;

    @Mock
    private GaugeMeterRegistrar gaugeMeterRegistrar;

    @Mock
    private CounterMeterRegistrar counterMeterRegistrar;

    @InjectMocks
    private AllMeterRegistrar allMeterRegistrar;

    @Test
    public void shouldRegisterAllMeters() {
        final CompositeMeterRegistry compositeMeterRegistry = mock(CompositeMeterRegistry.class);
        final List<SourceComponentPair> sourceComponentPairs = singletonList(new SourceComponentPair("test-source", "test-component"));

        final EventStreamGaugeMeter gaugeMeter = mock(EventStreamGaugeMeter.class);
        final List<EventStreamGaugeMeter> gaugeMeters = singletonList(gaugeMeter);

        final MetricsMeter counterMeter = mock(MetricsMeter.class);
        final List<MetricsMeter> counterMeters = singletonList(counterMeter);

        when(tagProvider.getSourceComponentPairs()).thenReturn(sourceComponentPairs);
        when(gaugeMeterFactory.createAllGaugeMetersForSourceAndComponents(sourceComponentPairs)).thenReturn(gaugeMeters);
        when(counterMeterFactory.createAllCounterMetersForSourceAndComponents(sourceComponentPairs)).thenReturn(counterMeters);

        // run
        allMeterRegistrar.registerAllMetersFor(compositeMeterRegistry);

        // verify
        verify(tagProvider).getSourceComponentPairs();
        verify(gaugeMeterFactory).createAllGaugeMetersForSourceAndComponents(sourceComponentPairs);
        verify(gaugeMeterRegistrar).registerGaugeMeter(gaugeMeter, compositeMeterRegistry);
        verify(counterMeterFactory).createAllCounterMetersForSourceAndComponents(sourceComponentPairs);
        verify(counterMeterRegistrar).registerCounterMeter(counterMeter, compositeMeterRegistry);
    }

    @Test
    public void shouldHandleEmptySourceComponentPairs() {
        final CompositeMeterRegistry compositeMeterRegistry = mock(CompositeMeterRegistry.class);
        final List<SourceComponentPair> sourceComponentPairs = emptyList();

        final List<EventStreamGaugeMeter> gaugeMeters = emptyList();
        final List<MetricsMeter> counterMeters = emptyList();

        when(tagProvider.getSourceComponentPairs()).thenReturn(sourceComponentPairs);
        when(gaugeMeterFactory.createAllGaugeMetersForSourceAndComponents(sourceComponentPairs)).thenReturn(gaugeMeters);
        when(counterMeterFactory.createAllCounterMetersForSourceAndComponents(sourceComponentPairs)).thenReturn(counterMeters);

        // run
        allMeterRegistrar.registerAllMetersFor(compositeMeterRegistry);

        // verify
        verify(tagProvider).getSourceComponentPairs();
        verify(gaugeMeterFactory).createAllGaugeMetersForSourceAndComponents(sourceComponentPairs);
        verify(counterMeterFactory).createAllCounterMetersForSourceAndComponents(sourceComponentPairs);

        // No meters should be registered
        verifyNoInteractions(gaugeMeterRegistrar);
        verifyNoInteractions(counterMeterRegistrar);
    }
}
