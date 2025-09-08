package uk.gov.justice.services.audit.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.metrics.micrometer.prometheus.TimerRegistrar;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditMetricsRecorderTest {

    @Mock
    private PrometheusMeterRegistry prometheusMeterRegistry;

    private AuditMetricsRecorder auditMetricsRecorder;

    @BeforeEach
    void setUp() {
        auditMetricsRecorder = new AuditMetricsRecorder(prometheusMeterRegistry, mock(TimerRegistrar.class));
    }

    @Test
    void shouldRegisterTimersAsPartOfConstructor() {
        final TimerRegistrar timerRegistrar = mock(TimerRegistrar.class);

        new AuditMetricsRecorder(prometheusMeterRegistry, timerRegistrar);

        verify(timerRegistrar).registerTimer("audit.artemis.success.latency", 0.5, 0.95, 0.99);
        verify(timerRegistrar).registerTimer("audit.artemis.failure.latency", 0.5, 0.95, 0.99);
        verify(timerRegistrar).registerTimer("audit.datalake.sync.success.latency", 0.5, 0.95, 0.99);
        verify(timerRegistrar).registerTimer("audit.datalake.sync.failure.latency", 0.5, 0.95, 0.99);
        verify(timerRegistrar).registerTimer("audit.datalake.async.success.latency", 0.5, 0.95, 0.99);
    }

    @Test
    void shouldRecordArtemisSuccessLatency() {
        final Timer.Sample sample = mock(Timer.Sample.class);
        final Timer registeredTimer = mock(Timer.class);
        when(prometheusMeterRegistry.timer("audit.artemis.success.latency")).thenReturn(registeredTimer);

        auditMetricsRecorder.recordArtemisSuccessLatency(sample);

        verify(sample).stop(registeredTimer);
    }

    @Test
    void shouldRecordArtemisFailureLatency() {
        final Timer.Sample sample = mock(Timer.Sample.class);
        final Timer registeredTimer = mock(Timer.class);
        when(prometheusMeterRegistry.timer("audit.artemis.failure.latency")).thenReturn(registeredTimer);

        auditMetricsRecorder.recordArtemisFailureLatency(sample);

        verify(sample).stop(registeredTimer);
    }

    @Test
    void shouldRecordDataLakeSyncSuccessLatency() {
        final Timer.Sample sample = mock(Timer.Sample.class);
        final Timer registeredTimer = mock(Timer.class);
        when(prometheusMeterRegistry.timer("audit.datalake.sync.success.latency")).thenReturn(registeredTimer);

        auditMetricsRecorder.recordDataLakeSyncSuccessLatency(sample);

        verify(sample).stop(registeredTimer);
    }

    @Test
    void shouldRecordDataLakeSyncFailureLatency() {
        final Timer.Sample sample = mock(Timer.Sample.class);
        final Timer registeredTimer = mock(Timer.class);
        when(prometheusMeterRegistry.timer("audit.datalake.sync.failure.latency")).thenReturn(registeredTimer);

        auditMetricsRecorder.recordDataLakeSyncFailureLatency(sample);

        verify(sample).stop(registeredTimer);
    }

    @Test
    void shouldRecordDataLakeASyncSuccessLatency() {
        final Timer.Sample sample = mock(Timer.Sample.class);
        final Timer registeredTimer = mock(Timer.class);
        when(prometheusMeterRegistry.timer("audit.datalake.async.success.latency")).thenReturn(registeredTimer);

        auditMetricsRecorder.recordDataLakeASyncSuccessLatency(sample);

        verify(sample).stop(registeredTimer);
    }

    @Test
    void shouldRecordDataLakeThroughput() {
        long fileSizeInBytes = 1024L;
        final Counter counter = mock(Counter.class);
        when(prometheusMeterRegistry.counter("audit.datalake.throughput.bytes")).thenReturn(counter);

        auditMetricsRecorder.recordDataLakeThroughput(fileSizeInBytes);

        verify(counter).increment(fileSizeInBytes);
    }
}