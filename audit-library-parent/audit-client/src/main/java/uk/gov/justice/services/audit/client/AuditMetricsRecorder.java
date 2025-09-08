package uk.gov.justice.services.audit.client;

import uk.gov.justice.services.metrics.micrometer.prometheus.TimerRegistrar;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

@Dependent
public class AuditMetricsRecorder {

    private static final String ARTEMIS_SUCCESS_LATENCY_TIMER_NAME = "audit.artemis.success.latency";
    private static final String ARTEMIS_FAILURE_LATENCY_TIMER_NAME = "audit.artemis.failure.latency";
    private static final String DATA_LAKE_SYNC_SUCCESS_LATENCY_TIMER_NAME = "audit.datalake.sync.success.latency";
    private static final String DATA_LAKE_ASYNC_SUCCESS_LATENCY_TIMER_NAME = "audit.datalake.async.success.latency";
    private static final String DATA_LAKE_SYNC_FAILURE_LATENCY_TIMER_NAME = "audit.datalake.sync.failure.latency";
    private static final String DATA_LAKE_THROUGHPUT_BYTES_COUNTER_NAME = "audit.datalake.throughput.bytes";

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @Inject
    public AuditMetricsRecorder(final PrometheusMeterRegistry prometheusMeterRegistry, final TimerRegistrar timerRegistrar) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
        registerTimers(timerRegistrar);
    }

    public Timer.Sample startTimer() {
        return Timer.start(prometheusMeterRegistry);
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
        prometheusMeterRegistry.counter(DATA_LAKE_THROUGHPUT_BYTES_COUNTER_NAME).increment(fileSizeInBytes);
    }

    private void recordMetrics(final Timer.Sample timer, final String timerName) {
        timer.stop(prometheusMeterRegistry.timer(timerName));
    }

    private void registerTimers(final TimerRegistrar timerRegistrar) {
        timerRegistrar.registerTimer(ARTEMIS_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(ARTEMIS_FAILURE_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_SYNC_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_SYNC_FAILURE_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
        timerRegistrar.registerTimer(DATA_LAKE_ASYNC_SUCCESS_LATENCY_TIMER_NAME, 0.5, 0.95, 0.99);
    }
}