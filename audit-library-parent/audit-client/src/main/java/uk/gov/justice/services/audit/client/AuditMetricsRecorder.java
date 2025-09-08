package uk.gov.justice.services.audit.client;

import uk.gov.justice.services.metrics.TimerRegistrar;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Dependent
public class AuditMetricsRecorder {

    private static final String ARTEMIS_SUCCESS_LATENCY_TIMER_NAME = "audit.artemis.success.latency";
    private static final String ARTEMIS_FAILURE_LATENCY_TIMER_NAME = "audit.artemis.failure.latency";
    private static final String DATA_LAKE_SYNC_SUCCESS_LATENCY_TIMER_NAME = "audit.datalake.sync.success.latency";
    private static final String DATA_LAKE_ASYNC_SUCCESS_LATENCY_TIMER_NAME = "audit.datalake.async.success.latency";
    private static final String DATA_LAKE_SYNC_FAILURE_LATENCY_TIMER_NAME = "audit.datalake.sync.failure.latency";
    private static final String DATA_LAKE_THROUGHPUT_BYTES_COUNTER_NAME = "audit.datalake.throughput.bytes";

    private final MeterRegistry meterRegistry;

    @Inject
    public AuditMetricsRecorder(final MeterRegistry meterRegistry, final TimerRegistrar timerRegistrar) {
        this.meterRegistry = meterRegistry;
        registerTimers(timerRegistrar);
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordArtemisSuccessLatency(Timer.Sample timer) {
        recordMetrics(timer, ARTEMIS_SUCCESS_LATENCY_TIMER_NAME);
    }

    public void recordArtemisFailureLatency(Timer.Sample timer) {
        recordMetrics(timer, ARTEMIS_FAILURE_LATENCY_TIMER_NAME);
    }

    public void recordDataLakeSyncSuccessLatency(Timer.Sample timer) {
        recordMetrics(timer, DATA_LAKE_SYNC_SUCCESS_LATENCY_TIMER_NAME);
    }

    public void recordDataLakeSyncFailureLatency(Timer.Sample timer) {
        recordMetrics(timer, DATA_LAKE_SYNC_FAILURE_LATENCY_TIMER_NAME);
    }

    public void recordDataLakeASyncSuccessLatency(Timer.Sample timer) {
        recordMetrics(timer, DATA_LAKE_ASYNC_SUCCESS_LATENCY_TIMER_NAME);
    }

    public void recordDataLakeThroughput(final long fileSizeInBytes) {
        meterRegistry.counter(DATA_LAKE_THROUGHPUT_BYTES_COUNTER_NAME).increment(fileSizeInBytes);
    }

    private void recordMetrics(final Timer.Sample timer, final String timerName) {
        timer.stop(meterRegistry.timer(timerName));
    }

    private void registerTimers(final TimerRegistrar timerRegistrar) {
        timerRegistrar.registerTimer(ARTEMIS_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(ARTEMIS_FAILURE_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_SYNC_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_SYNC_FAILURE_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_ASYNC_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
    }
}