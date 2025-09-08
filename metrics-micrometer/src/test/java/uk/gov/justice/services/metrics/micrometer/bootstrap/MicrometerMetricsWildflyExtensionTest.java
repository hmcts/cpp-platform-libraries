package uk.gov.justice.services.metrics.micrometer.bootstrap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.framework.utilities.cdi.CdiInstanceResolver;
import uk.gov.justice.services.metrics.micrometer.config.MetricsConfiguration;

import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MicrometerMetricsWildflyExtensionTest {

    @Mock
    private CdiInstanceResolver cdiInstanceResolver;

    @InjectMocks
    private MicrometerMetricsWildflyExtension micrometerMetricsWildflyExtension;

    @Test
    public void shouldBootstrapMicrometerMetricsOnApplicationStartup() throws Exception {

        final AfterDeploymentValidation event = mock(AfterDeploymentValidation.class);
        final BeanManager beanManager = mock(BeanManager.class);
        final MetricsConfiguration metricsConfiguration = mock(MetricsConfiguration.class);
        final MicrometerMetricsBootstrapper micrometerMetricsBootstrapper = mock(MicrometerMetricsBootstrapper.class);

        when(cdiInstanceResolver.getInstanceOf(
                MetricsConfiguration.class,
                beanManager)).thenReturn(metricsConfiguration);
        when(metricsConfiguration.micrometerMetricsEnabled()).thenReturn(true);
        when(cdiInstanceResolver.getInstanceOf(
                MicrometerMetricsBootstrapper.class,
                beanManager)).thenReturn(micrometerMetricsBootstrapper);

        micrometerMetricsWildflyExtension.afterDeploymentValidation(event, beanManager);

        verify(micrometerMetricsBootstrapper).bootstrapMetrics();
    }

    @Test
    public void shouldNotBootstrapMicrometerMetricsOnIfMetricsConfiguredNotEnabled() throws Exception {

        final AfterDeploymentValidation event = mock(AfterDeploymentValidation.class);
        final BeanManager beanManager = mock(BeanManager.class);
        final MetricsConfiguration metricsConfiguration = mock(MetricsConfiguration.class);

        when(cdiInstanceResolver.getInstanceOf(
                MetricsConfiguration.class,
                beanManager)).thenReturn(metricsConfiguration);
        when(metricsConfiguration.micrometerMetricsEnabled()).thenReturn(false);

        micrometerMetricsWildflyExtension.afterDeploymentValidation(event, beanManager);

        verify(cdiInstanceResolver, never()).getInstanceOf(
                MicrometerMetricsBootstrapper.class,
                beanManager);
    }
}