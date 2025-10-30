package uk.gov.justice.services.audit.client;

import static java.lang.Boolean.parseBoolean;
import static reactor.core.scheduler.Schedulers.boundedElastic;

import uk.gov.justice.services.common.configuration.Value;
import uk.gov.justice.services.core.audit.AuditClient;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.json.JsonObject;

import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

/**
 * A hybrid audit client that audits events via both storage and remote.
 */
@ApplicationScoped
@Alternative
@Priority(6)
public class HybridAuditClient implements AuditClient {

    @Inject
    @Value(key = "audit.storage.enabled", defaultValue = "false")
    private String auditStorageEnabled;

    //Should never be turned off until POC results are reviewed
    @Inject
    @Value(key = "audit.storage.async", defaultValue = "true")
    private String auditStorageAsync;

    @Inject
    private RemoteAuditClient remoteAuditClient;

    @Inject
    private AuditEntryContentCreator auditEntryContentCreator;

    @Inject
    private AzureDataLakeServiceClient azureDataLakeServiceClient;

    @Inject
    private Logger logger;

    @Inject
    private AuditMetricsRecorder auditMetricsRecorder;

    /**
     * Audits an event via both storage and remote.
     *
     * @param envelope  the event envelope
     * @param component the component that raised the event
     */
    @Override
    public void auditEntry(final JsonEnvelope envelope, final String component) {
        auditEntryViaArtemis(envelope, component);
        auditEntryViaStorage(envelope, component);
    }

    private void auditEntryViaArtemis(final JsonEnvelope envelope, final String component) {
        try {
            final Timer.Sample eventSendTimer = auditMetricsRecorder.startTimer();

            boolean eventSent = sendAuditEvent(envelope, component);

            if (eventSent) {
                auditMetricsRecorder.recordArtemisSuccessLatency(eventSendTimer);
            } else {
                auditMetricsRecorder.recordArtemisFailureLatency(eventSendTimer);;
            }
        } catch (Exception e) {
            logger.error("Error occurred while recording audit event (via artemis) metrics", e);
        }
    }

    private boolean sendAuditEvent(final JsonEnvelope envelope, final String component) {
        try {
            remoteAuditClient.auditEntry(envelope, component);
            return true;
        } catch (Exception e) {
            logger.error(String.format("Failed to send audit entry event for %s", envelope.toString()), e);
            return false;
        }
    }

    private void auditEntryViaStorage(final JsonEnvelope envelope, final String component) {
        try {
            final boolean isAuditStorageEnabled = parseBoolean(this.auditStorageEnabled);
            final boolean isAuditStorageAsync = parseBoolean(this.auditStorageAsync);

            if (isAuditStorageEnabled) {
                if (isAuditStorageAsync) {
                    logger.debug("Audit entry via storage async");
                    uploadToStorageAsync(envelope, component);
                } else {
                    logger.debug("Audit entry via storage sync");
                    uploadToStorageSync(envelope, component);
                }
            } else {
                logger.debug("Audit entry via storage disabled");
            }
        } catch (Throwable t) {
            logger.error("Error while storing audit event via storage", t);
        }
    }

    private void uploadToStorageAsync(final JsonEnvelope envelope, final String component) {
        final Timer.Sample asyncInvokerTimer = auditMetricsRecorder.startTimer();
        Mono.fromRunnable(() -> uploadToStorageSync(envelope, component))
                .subscribeOn(boundedElastic())
                .subscribe();
        auditMetricsRecorder.recordDataLakeASyncSuccessLatency(asyncInvokerTimer);
    }

    private void uploadToStorageSync(final JsonEnvelope envelope, final String component) {
        final JsonObject auditEntryContent = auditEntryContentCreator.create(envelope, component);
        final DataLakeFileObject envelopePayloadJsonObject = new DataLakeFileObject(auditEntryContent);
        final String filePath = envelopePayloadJsonObject.getFilePath();
        final String fileName = envelopePayloadJsonObject.getFileName();
        final String fileSize = String.valueOf(envelopePayloadJsonObject.getFileSize());
        final Timer.Sample uploadTimer = auditMetricsRecorder.startTimer();

        try {
            azureDataLakeServiceClient.uploadToStorage(envelopePayloadJsonObject);
            logger.info("Audit entry upload to Data Lake successful. File {}/{}, size {}B", filePath, fileName, fileSize);

            auditMetricsRecorder.recordDataLakeSyncSuccessLatency(uploadTimer);
            auditMetricsRecorder.recordDataLakeThroughput(envelopePayloadJsonObject.getFileSize());
        } catch (final Throwable t) {
            auditMetricsRecorder.recordDataLakeSyncFailureLatency(uploadTimer);
            final String errorMessage = "Audit entry upload to Data Lake failed. File %s/%s, size %sB".formatted(filePath, fileName, fileSize);
            throw new AuditEntryUploadToAzureDataLakeFailure(errorMessage, t);
        }
    }
}
