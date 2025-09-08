package uk.gov.justice.services.metrics.micrometer.bootstrap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.eventstore.metrics.tags.TagProvider;
import uk.gov.justice.services.metrics.micrometer.registry.MetricsRegistrar;

import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class MicrometerMetricsBootstrapperTest {

    @Mock
    private CompositeMeterRegistry compositeMeterRegistry;

    @Mock
    private MetricsRegistrar metricsRegistrar;

    @Mock
    private AllMeterRegistrar allMeterRegistrar;

    @Mock
    private Logger logger;

    @Mock
    private TagProvider tagProvider;

    @InjectMocks
    private MicrometerMetricsBootstrapper micrometerMetricsBootstrapper;

    @Test
    public void shouldCreateRegistriesAndGaugesIfMetricsEnabled() throws Exception {
        final List<Tag> commonTags = List.of(
                Tag.of("service", "test-service"),
                Tag.of("env", "test-env")
        );

        final MeterRegistry.Config config = mock(MeterRegistry.Config.class);

        when(tagProvider.getCommonTags()).thenReturn(commonTags);
        when(compositeMeterRegistry.config()).thenReturn(config);

        // run
        micrometerMetricsBootstrapper.bootstrapMetrics();

        // verify
        verify(logger).info("Starting micrometer metrics");
        verify(compositeMeterRegistry).config();
        verify(config).commonTags(commonTags);
        verify(allMeterRegistrar).registerAllMetersFor(compositeMeterRegistry);
        verify(metricsRegistrar).addRegistry(compositeMeterRegistry);
    }
}
